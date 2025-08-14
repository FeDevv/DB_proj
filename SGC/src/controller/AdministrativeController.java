package controller;

import exception.DataAccessException;
import model.Utils.InternalConflictsUtils;
import model.dao.*;
import model.domain.*;
import view.AdministrativeView;
import view.CommonView;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static view.CommonView.askCourseSelection;
import static view.CommonView.showStudentsList;

public class AdministrativeController extends UserController{

    public AdministrativeController(Credentials creds) {
        super(creds);
    }

    @Override
    protected void showWelcomeMessage() {
        AdministrativeView.showWelcomeMessage(creds.getUsername());
    }

    @Override
    protected int showMenu() {
        return AdministrativeView.showMenu();
    }

    @Override
    protected boolean handleMenuChoice(int choice) throws IOException, DataAccessException {
        switch (choice) {
            case 1 -> {
                //stampa studenti di un corso
                printStudentsFromCourse();
            }
            case 2 -> {
                //stampa tutti gli insegnante
                printAllTeachers();
            }
            case 3 -> {
                //stampa tutti i membri del personale amministrativo
                printAllMembers();
            }
            case 4 -> {
                //visualizza corsi per insegnante
                coursesByTeacher();
            }
            case 5 -> {
                //inserimento nuovo corso
                insertNewCourse();
            }
            case 6 -> {
                //iscrizione studente a corso
                enrollStudentToCourse();
            }
            case 7 -> {
                //salva assenza di uno studente
                recordStudentAbsence();
            }
            case 8 -> {
                //crea un nuovo insegnante e inseriscilo nel DB
                addTeacher();
            }
            case 9 -> CommonView.showMessage("Assegnazione insegnante a corso...");
            case 10 -> {
                generateMonthlyReport();
            }
            case 11 -> CommonView.showMessage("Visualizzazione assenze classe...");
            case 0 -> { return false; } // Logout
            default -> AdministrativeView.showInvalidOption();
        }
        return true;
    }

    @Override
    protected void printStudentsFromCourse() {
        //Stampa elenco studenti del corso
        Course course = askCourseSelection();
        if(course == null) {
            CommonView.showMessage("Selezione corso non valida");
        } else {
            List<Student> students = fetchStudentsForCourse(course.getCourseID(), course.getLevel());
            showStudentsList(students);
        }
    }

    protected void printAllTeachers() {
        TeacherDAO teacherDAO = new TeacherDAO();
        try {
            List<Teacher> teachers = teacherDAO.getAllTeachers(creds);
            AdministrativeView.showTeachersList(teachers);
        } catch (DataAccessException e) {
            CommonView.showMessage("Errore nel recupero insegnanti: " + e.getMessage());
        }
    }

    protected void printAllMembers() {
        AdministratorDAO administratorDAO = new AdministratorDAO();
        try {
            List<Administrator> admins = administratorDAO.getAllAdministrators(creds);
            AdministrativeView.showAdministratorsList(admins);
        } catch (DataAccessException e) {
            CommonView.showMessage("Errore nel recupero personale: " + e.getMessage());
        }
    }

    protected void coursesByTeacher() {
        try {
            // Utilizza un metodo dedicato per ottenere l'ID insegnante
            int teacherId = getTeacherIdFromUser();

            CourseDAO assignedCoursesDAO = new CourseDAO();
            List<Course> courses = CourseDAO.getCoursesByTeacher(teacherId, creds);

            // Controlla se ci sono corsi prima di mostrarli
            if (courses == null || courses.isEmpty()) {
                CommonView.showMessage("Nessun corso trovato per l'insegnante con ID: " + teacherId);
            } else {
                AdministrativeView.showTeacherCourses(courses, teacherId);
            }

        } catch (DataAccessException e) {
            CommonView.showMessage("Errore nel database: " + e.getMessage());
        } catch (NumberFormatException e) {
            CommonView.showMessage("ID insegnante non valido. Inserisci un numero.");
        } catch (IOException e) {
            CommonView.showMessage("Errore di I/O durante la lettura dell'input.");
        } catch (Exception e) {
            CommonView.showMessage("Errore imprevisto: " + e.getMessage());
        }
    }

    //funzione ausiliaria da mettere tipo in Utils
    private int getTeacherIdFromUser() throws IOException {
        while (true) {
            try {
                CommonView.showMessage("Inserisci l'ID dell'insegnante: ");
                String input = CommonView.getGenericString().trim();

                if (input.isEmpty()) {
                    CommonView.showMessage("Input vuoto. Riprova.");
                    continue;
                }

                int teacherId = Integer.parseInt(input);

                if (teacherId <= 0) {
                    CommonView.showMessage("ID insegnante non valido. Deve essere un numero positivo.");
                    continue;
                }

                return teacherId;

            } catch (NumberFormatException e) {
                CommonView.showMessage("Formato non valido. Inserisci un numero intero.");
            }
        }
    }

    private void insertNewCourse() {
        try {
            Course newCourse = AdministrativeView.inputCourseDetails();
            List<Lesson> lessons = AdministrativeView.inputLessonsForCourse(newCourse.getLevel());

            if (lessons.isEmpty()) {
                CommonView.showMessage("Nessuna lezione inserita. Operazione annullata.");
                return;
            }

            // Controlla conflitti interni
            if (InternalConflictsUtils.hasInternalConflicts(lessons)) {
                CommonView.showMessage("Conflitti tra le lezioni inserite. Correggi gli orari.");
                return;
            }

            LessonDAO lessonDAO = new LessonDAO();
            List<Lesson> conflictingLessons = new ArrayList<>();

            for (Lesson lesson : lessons) {
                if (lessonDAO.hasOverlappingLesson(lesson, creds)) {
                    conflictingLessons.add(lesson);
                }
            }

            if (!conflictingLessons.isEmpty()) {
                AdministrativeView.showConflictingLessons(conflictingLessons);
                return;
            }

            CourseDAO courseDAO = new CourseDAO();

            // Aggiorna le lezioni con l'ID corso generato
            int courseId = courseDAO.insertCourse(newCourse, creds); // Genera l'ID corso
            for (Lesson lesson : lessons) {
                lesson.setProgressiveCode(courseId);
            }

            lessonDAO.insertLessons(lessons, creds);

            CommonView.showMessage("Corso creato con successo con ID " + courseId +
                    " e " + lessons.size() + " lezioni!");

        } catch (IOException | DataAccessException e) {
            CommonView.showMessage("Errore: " + e.getMessage());
        }
    }

    private void enrollStudentToCourse() {
        try {
            Student newStudent = null;
            if (AdministrativeView.askStudentStatus() == 1){
                //nuovo studente
                newStudent = AdministrativeView.inputStudentDetails();
                StudentDAO studentDAO = new StudentDAO();
                studentDAO.createStudent(newStudent, creds);
                CommonView.showMessage("Studente creato con ID: " + newStudent.getStudentID());
            } else if (AdministrativeView.askStudentStatus() == 2){
                //studente gia esistente
                StudentDAO studentDAO = new StudentDAO();
                CommonView.showMessage("Inserisci l'ID dello studente");
                int id = Integer.parseInt(CommonView.getGenericString().trim());
                newStudent = studentDAO.getStudentByID(id, creds);
                if (newStudent == null) {
                    CommonView.showMessage("Studente non trovato con ID: " + id);
                    return;
                }
            }

            // 2. dai una lista di corsi attivi e fai scegliere il corso al quale iscriversi
            CourseDAO courseDAO = new CourseDAO();
            List<Course> courses = courseDAO.getActiveCourses(creds);

            Course chosenCourse = AdministrativeView.chooseCourse(courses);

            // 3. Iscrizione
            EnrollmentDAO enrollmentDAO = new EnrollmentDAO();
            assert newStudent != null;
            assert chosenCourse != null;
            enrollmentDAO.enrollStudent(
                    newStudent.getStudentID(),
                    chosenCourse.getCourseID(),
                    chosenCourse.getLevel(),
                    creds
            );

            CommonView.showMessage("Iscrizione completata con successo!");

        } catch (IOException e) {
            CommonView.showMessage("Errore input: " + e.getMessage());
        } catch (DataAccessException e) {
            CommonView.showMessage("Errore database: " + e.getMessage());
        }
    }

    private void addTeacher() {
        try {
            Teacher newTeacher = AdministrativeView.inputTeacherDetails();
            TeacherDAO teacherDAO = new TeacherDAO();
            int generatedId = teacherDAO.insertTeacher(newTeacher, creds);

            // Aggiorna l'oggetto con l'ID generato dal DB
            newTeacher.setTeacherID(generatedId);

            CommonView.showMessage("Nuovo insegnante con ID: " + generatedId + " creato con successo!");
        } catch (DataAccessException e) {
            CommonView.showMessage("Errore di accesso ai dati: " + e.getMessage());
        } catch (IOException e) {
            CommonView.showMessage("Errore di input: " + e.getMessage());
        } catch (Exception e) {
            CommonView.showMessage("Errore imprevisto: " + e.getMessage());
        }
    }

    private void generateMonthlyReport() {
        TeacherDAO teacherDAO = new TeacherDAO();
        ReportDAO reportDAO = new ReportDAO();

        try {
            // 1. Recupera tutti gli insegnanti
            List<Teacher> teachers = teacherDAO.getAllTeachers(creds);

            // 2. Mostra la lista e seleziona gli insegnanti per il report
            AdministrativeView.showTeachersList(teachers);
            List<Teacher> selectedTeachers = AdministrativeView.getSomeTeachers(teachers);

            // 3. Validazione: almeno un insegnante selezionato
            if (selectedTeachers.isEmpty()) {
                CommonView.showMessage("Nessun insegnante selezionato. Operazione annullata.");
                return;
            }

            // 4. Inserimento dati del report
            String reportData = AdministrativeView.insertMontlyReportData(selectedTeachers);

            // 5. Creazione oggetto report (primo giorno del mese corrente)
            MonthlyReport report = new MonthlyReport(
                    LocalDate.now().withDayOfMonth(1),
                    reportData
            );

            // 6. Estrai ID insegnanti
            List<Integer> teacherIDs = selectedTeachers.stream()
                    .map(Teacher::getTeacherID)
                    .collect(Collectors.toList());

            // 7. Inserimento nel database
            int reportID = reportDAO.insertMonthlyReport(report, creds.getID(), teacherIDs, creds);

            CommonView.showMessage("Report mensile creato con ID: " + reportID);

        } catch (DataAccessException e) {
            CommonView.showMessage("Errore di accesso al database: " + e.getMessage());
        } catch (IOException e) {
            CommonView.showMessage("Errore di input/output: " + e.getMessage());
        } catch (Exception e) {
            CommonView.showMessage("Errore imprevisto: " + e.getMessage());
        }
    }

    @Override
    protected void showExitMessage() {
        CommonView.showExitMessage(creds.getUsername());
    }
}
