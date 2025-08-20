package controller;

import exception.DataAccessException;
import model.Utils.AssignmentGeneratorUtils;
import model.Utils.InternalConflictsUtils;
import model.dao.*;
import model.domain.*;
import view.AdministrativeView;
import view.CommonView;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
    protected boolean handleMenuChoice(int choice) throws IOException, DataAccessException, SQLException {
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
            case 9 -> {
                //assegna insegnante a corso
                assigningTeacher();
            }
            case 10 -> {
                generateMonthlyReport();
            }
            case 11 -> {
                showCourseAbsences();
            }
            case 12 -> {
                handleDeleteMenu();
            }
            case 0 -> { return false; } // Logout
            default -> AdministrativeView.showInvalidOption();
        }
        return true;
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
            int reportID = ReportDAO.insertMonthlyReport(report, teacherIDs, creds);

            CommonView.showMessage("Report mensile creato con ID: " + reportID);

        } catch (DataAccessException e) {
            CommonView.showMessage("Errore di accesso al database: " + e.getMessage());
        } catch (IOException e) {
            CommonView.showMessage("Errore di input/output: " + e.getMessage());
        } catch (Exception e) {
            CommonView.showMessage("Errore imprevisto: " + e.getMessage());
        }
    }

    private void assigningTeacher() throws DataAccessException, IOException, SQLException {
        try {
            TeacherDAO teacherDAO = new TeacherDAO();
            List<Teacher> teachers = teacherDAO.getAllActiveTeachers(creds);

            CourseDAO courseDAO = new CourseDAO();
            List<Course> courses = courseDAO.getActiveCourses(creds);

            CommonView.showMessage("Scegli gli insegnanti / l'insegnante");
            AdministrativeView.showTeachersList(teachers);
            List<Teacher> chosenTeachers = AdministrativeView.getSomeTeachers(teachers);

            CommonView.showMessage("scegli il corso");
            AdministrativeView.showSelectedCourses(courses);
            Course chosenCourse = AdministrativeView.chooseCourse(courses);

            //prendi tutte le lezioni del corso per
            LessonDAO lessonDAO = new LessonDAO();
            if (chosenCourse == null) {
                CommonView.showMessage("Errore nella scelta del corso.");
                return;
            }
            List<Lesson> lessons = LessonDAO.getLessonsByCourse(chosenCourse.getCourseID(), chosenCourse.getLevel(), creds);

            //mostra le lezioni
            AdministrativeView.showLessons(lessons);

            //controlla no overlap con orari insegnanti
            AssignmentDAO.checkScheduleConflicts(chosenTeachers, lessons, creds);

            //crea assegnazioni se non ci sono conflitti
            List<Assignment> assignments = AssignmentGeneratorUtils.generateAssignments(chosenTeachers, lessons, chosenCourse.getCourseID());
            AssignmentDAO.insertAssignments(assignments, creds);
        } catch (DataAccessException | IOException | SQLException e) {
            CommonView.showMessage("Errore durante l'assegnazione: " + e.getMessage());
        }

    }

    @Override
    protected List<Student> loadStudentsForCourse(Course course) throws DataAccessException {
        StudentDAO studentDAO = new StudentDAO();
        return studentDAO.getStudentsByCourse(
                course.getCourseID(),
                course.getLevel(),
                null,
                creds
        );
    }

    private void handleDeleteMenu() throws IOException, DataAccessException {
        boolean exit = false;
        int choice;

        while (!exit) {
            AdministrativeView.showDeleteMenu();
            choice = CommonView.getGenericInteger();
            switch (choice) {
                case 1 -> {
                    //cancellazione corso
                    deleteCourse();
                }
                case 2 -> {
                    //cancellazione studente
                    deleteStudent();
                }
                case 3 -> {
                    //cancellazione insegnante
                    deleteTeacher();
                }
                case 4 -> {
                    //cancellazione admin
                    deleteAdministrator();
                }
                case 5 -> {
                    deleteWeeklyReport();
                }
                case 6 -> {
                    deleteMonthlyReport();
                }
                case 7 -> {
                    deleteLesson();
                }
                case 0 -> {exit = true;}
                default -> {CommonView.showMessage("Scelta non valida.");}
            }
        }
    }

    private void deleteCourse() {
        try {
            // 1. Ottenere l'ID del corso
            CommonView.showMessage("Inserisci l'ID del corso da eliminare:");
            int courseID = CommonView.getGenericInteger();

            // 2. Ottenere il livello del corso
            CommonView.showMessage("Scegli il livello del corso:");
            LevelName level = AdministrativeView.choseLevel();

            // 3. Verificare l'esistenza del corso
            CourseDAO courseDAO = new CourseDAO();
            Course course = courseDAO.getCourseByIdAndLevel(courseID, level, creds);

            if (course == null) {
                CommonView.showMessage("Nessun corso trovato con ID " + courseID + " e livello " + level);
                return;
            }

            AdministrativeView.showCourseDetails(course);

            // 5. Richiedere conferma
            boolean confirm = AdministrativeView.confirmAction(
                    "Sei sicuro di voler eliminare questo corso e tutti i dati associati?\n" +
                            "Questa operazione non può essere annullata."
            );

            if (confirm) {
                courseDAO.deleteCourse(courseID, level, creds);
                CommonView.showMessage("Corso eliminato con successo.");
            } else {
                CommonView.showMessage("Operazione annullata.");
            }

        } catch (DataAccessException e) {
            CommonView.showMessage("Errore di accesso al database: " + e.getMessage());
        } catch (IOException e) {
            CommonView.showMessage("Errore di input: " + e.getMessage());
        }
    }

    private void deleteStudent() throws IOException, DataAccessException {
        try{
            CommonView.showMessage("inserisci l'ID dello studente");
            int studentID = CommonView.getGenericInteger();
            StudentDAO studentDAO = new StudentDAO();
            Student student = studentDAO.getStudentByID(studentID, creds);

            if (student == null) {
                CommonView.showMessage("Studente non trovato con ID: " + studentID);
                return;
            }

            AdministrativeView.showStudentDetails(student);

            boolean confirm = AdministrativeView.confirmAction("Sei sicuro di voler eliminare questo studente e tutti i dati associati?\n" +
                    "Questa operazione non può essere annullata.");

            if (confirm) {
                studentDAO.deleteStudent(studentID, creds);
                CommonView.showMessage("Studente eliminato con successo.");
            } else {
                CommonView.showMessage("Operazione annullata.");
            }

        } catch (DataAccessException e) {
            CommonView.showMessage("Errore durante l'eliminazione: " + e.getMessage());
        }
    }

    private void deleteTeacher() throws IOException, DataAccessException {
        try{
            // 1. Ottenere l'ID dell'insegnante
            CommonView.showMessage("Inserisci l'ID dell'insegnante da eliminare:");
            int teacherID = CommonView.getGenericInteger();

            // 2. Verificare l'esistenza dell'insegnante
            TeacherDAO teacherDAO = new TeacherDAO();
            Teacher teacher = teacherDAO.getTeacherById(teacherID, creds);

            if (teacher == null) {
                CommonView.showMessage("Nessun insegnante trovato con ID " + teacherID);
                return;
            }

            AdministrativeView.showTeacherDetails(teacher);

            boolean confirm = AdministrativeView.confirmAction(
                    "Sei sicuro di voler eliminare questo insegnante e tutti i dati associati?\n" +
                            "Questa operazione non può essere annullata."
            );

            if (confirm) {
                teacherDAO.deleteTeacher(teacherID, creds);
                CommonView.showMessage("Insegnante eliminato con successo.");
            } else {
                CommonView.showMessage("Operazione annullata.");
            }

        }catch (DataAccessException e) {
            CommonView.showMessage("Errore di accesso al database: " + e.getMessage());
        } catch (IOException e) {
            CommonView.showMessage("Errore di input: " + e.getMessage());
        }
    }

    private void deleteAdministrator() throws IOException, DataAccessException {
        try{
            // 1. Ottenere l'ID dell'amministratore
            CommonView.showMessage("Inserisci l'ID dell'amministratore da eliminare:");
            int adminID = CommonView.getGenericInteger();

            // 2. Verificare l'esistenza dell'amministratore
            AdministratorDAO adminDAO = new AdministratorDAO();
            Administrator admin = adminDAO.getAdministratorById(adminID, creds);

            if (admin == null) {
                CommonView.showMessage("Nessun amministratore trovato con ID " + adminID);
                return;
            }

            AdministrativeView.showAdministratorDetails(admin);

            if (adminDAO.getAdministratorCount(creds) <= 1) {
                CommonView.showMessage("Impossibile eliminare l'ultimo amministratore del sistema.");
                return;
            }

            boolean confirm = AdministrativeView.confirmAction(
                    "Sei sicuro di voler eliminare questo amministratore?\n" +
                            "Questa operazione non può essere annullata."
            );

            if (confirm) {
                adminDAO.deleteAdministrator(adminID, creds);
                CommonView.showMessage("Amministratore eliminato con successo.");
            } else {
                CommonView.showMessage("Operazione annullata.");
            }

        }catch (DataAccessException e) {
            CommonView.showMessage("Errore di accesso al database: " + e.getMessage());
        } catch (IOException e) {
            CommonView.showMessage("Errore di input: " + e.getMessage());
        }

    }

    private void deleteWeeklyReport() throws IOException, DataAccessException {
        try{
            CommonView.showMessage("Inserisci l'ID del report settimanale da rimuovere:");
            int reportID = CommonView.getGenericInteger();

            WeeklyReport report = ReportDAO.getWeeklyReportByID(reportID, creds);

            if (report == null) {
                CommonView.showMessage("Nessun report trovato con ID " + reportID);
                return;
            }

            AdministrativeView.showWeeklyReportDetails(report);

            boolean confirm = AdministrativeView.confirmAction(
                    "Sei sicuro di voler eliminare questo report?\n" +
                            "Questa operazione non può essere annullata."
            );

            if (confirm) {
                ReportDAO.deleteWeeklyReport(reportID, creds);
                CommonView.showMessage("Report settimanale eliminato con successo.");
            } else {
                CommonView.showMessage("Operazione annullata.");
            }
        }catch (DataAccessException e) {
            CommonView.showMessage("Errore di accesso al database: " + e.getMessage());
        }catch (IOException e) {
            CommonView.showMessage("Errore di input: " + e.getMessage());
        }
    }

    private void deleteMonthlyReport() throws IOException, DataAccessException {
        try {
            CommonView.showMessage("Inserisci l'ID del report mensile da rimuovere:");
            int reportID = CommonView.getGenericInteger();

            MonthlyReport report = ReportDAO.getMonthlyReportByID(reportID, creds);

            if (report == null) {
                CommonView.showMessage("Nessun report trovato con ID " + reportID);
                return;
            }

            AdministrativeView.showMonthlyReportDetails(report);

            boolean confirm = AdministrativeView.confirmAction(
                    "Sei sicuro di voler eliminare questo report?\n" +
                            "Questa operazione non può essere annullata."
            );

            if (confirm) {
                ReportDAO.deleteMonthlyReport(reportID, creds);
                CommonView.showMessage("Report mensile eliminato con successo.");
            } else {
                CommonView.showMessage("Operazione annullata.");
            }
        }catch (DataAccessException e) {
            CommonView.showMessage("Errore di accesso al database: " + e.getMessage());
        }catch (IOException e) {
            CommonView.showMessage("Errore di input: " + e.getMessage());
        }

    }

    private void deleteLesson()  throws IOException, DataAccessException {
        try {
            // 1. Ottenere l'ID del corso
            CommonView.showMessage("Inserisci l'ID del corso da eliminare:");
            int courseID = CommonView.getGenericInteger();

            // 2. Ottenere il livello del corso
            CommonView.showMessage("Scegli il livello del corso:");
            LevelName level = AdministrativeView.choseLevel();

            // 3. Verificare l'esistenza del corso
            CourseDAO courseDAO = new CourseDAO();
            Course course = courseDAO.getCourseByIdAndLevel(courseID, level, creds);

            if (course == null) {
                CommonView.showMessage("Nessun corso trovato con ID " + courseID + " e livello " + level);
                return;
            }

            CommonView.showMessage("Scegli la lezione da eliminare");

            LessonDAO lessonDAO = new LessonDAO();
            List<Lesson> lessons = LessonDAO.getLessonsByCourse(courseID, level, creds);

            Lesson lesson = AdministrativeView.chooseLesson(lessons);

            boolean confirm = AdministrativeView.confirmAction(
                    "Sei sicuro di voler eliminare questa lezione?\n" +
                            "Questa operazione non può essere annullata."
            );

            if (confirm) {
                assert lesson != null;  //teoricamente gia controllato in chooseLesson, l'IDE me lo segna altrimenti
                LessonDAO.deleteLesson(lesson, creds);
                AssignmentDAO.deleteAssignment(lesson, creds);
                CommonView.showMessage("lezione eliminata con successo.");
            } else {
                CommonView.showMessage("Operazione annullata.");
            }
        }catch (DataAccessException e) {
            CommonView.showMessage("Errore di accesso al database: " + e.getMessage());
        }catch (IOException e) {
            CommonView.showMessage("Errore di input: " + e.getMessage());
        }
    }
}
