package controller;

import exception.ApplicationException;
import exception.DataAccessException;
import model.dao.*;
import model.domain.*;
import view.AdministrativeView;
import view.CommonView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static model.Utils.AbsenceUtils.isAbsenceOnLessonDay;
import static view.CommonView.askCourseSelection;
import static view.CommonView.showStudentsList;


public abstract class UserController {

    protected Credentials creds;

    public UserController(Credentials creds) {
        this.creds = creds;
    }

    //metodo principale comune a tutti i ruoli
    public void start() throws IOException, DataAccessException, SQLException {
        boolean running = true;
        showWelcomeMessage();

        while (running) {
            int choice = showMenu();
            running = handleMenuChoice(choice);
        }

        showExitMessage();
    }

    protected abstract void showWelcomeMessage();
    protected abstract int showMenu();

    //CHECKED
    protected abstract boolean handleMenuChoice(int choice) throws IOException, DataAccessException, SQLException;
    //CHECKED
    protected void showExitMessage() {
        CommonView.showExitMessage(creds.getUsername());
    }
    //CHECKED
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
    //CHECKED
    protected List<Student> fetchStudentsForCourse(int progressiveCode, LevelName level) {
        try {
            // se l'utente è insegnante uso l'ID contenuto in Credentials come filtro,
            // altrimenti teacherId = null -> DAO restituisce tutti gli studenti del corso
            Integer teacherId = (creds.getRole() == Role.INSEGNANTE) ? creds.getID() : null;

            StudentDAO studentDAO = new StudentDAO();
            return studentDAO.getStudentsByCourse(progressiveCode, level, teacherId, creds);

        } catch (DataAccessException e) {
            // Propagazione dell'errore al chiamante
            throw new ApplicationException("Impossibile recuperare gli studenti", e);
        }
    }
    //CHECKED
    protected void recordStudentAbsence() {
        try {
            // Input dati assenza
            Absence absence = CommonView.inputAbsenceDetails();

            //lista di corsi del ragazzo
            List<Course> courses = CourseDAO.getCoursesByStudent(absence.getStudentId(), creds);

            //lista di tutte le lezioni del tagazzo
            List<Lesson> lessons = new ArrayList<>();
            for (Course course : courses) {
                 List<Lesson> addLessons = LessonDAO.getLessonsByCourse(course.getCourseID(), course.getLevel(), creds);
                lessons.addAll(addLessons);
            }

            //se l'assenza è messa ad un giorno che non ci sta lezione fermo tutto.
            if(!isAbsenceOnLessonDay(absence, lessons)){
                CommonView.showMessage("l'assenza inserita non corrisponde ad alcuna lezione a cui partecipa lo studente.");
                return;
            }

            if (creds.getRole() == Role.INSEGNANTE) {
                // Verifica sicurezza per insegnanti con metodo efficiente
                CourseDAO courseDAO = new CourseDAO();
                if (!courseDAO.hasSharedActiveCourse(
                        creds.getID(),
                        absence.getStudentId(),
                        creds)) {

                    CommonView.showMessage("Operazione non permessa: " +
                            "lo studente non è nei tuoi corsi attivi!");
                    return;
                }
            }

            // Registrazione assenza
            AbsenceDAO absenceDAO = new AbsenceDAO();

            absenceDAO.recordAbsence(absence, creds);

            CommonView.showMessage("Assenza registrata con successo!");

        } catch (Exception e) {
            CommonView.showMessage("Errore: " + e.getMessage());
        }
    }
    //CHECKED
    protected final void showCourseAbsences() {
        try {
            CourseDAO courseDAO = new CourseDAO();
            List<Course> courses = courseDAO.getActiveCourses(creds);

            Course chosenCourse = AdministrativeView.chooseCourse(courses);

            if (chosenCourse == null) {
                CommonView.showMessage("Errore nella scelta del corso.");
                return;
            }

            int activationYear = chosenCourse.getActivationDate().getYear();

            // 🔹 delega alle sottoclassi
            List<Student> students = loadStudentsForCourse(chosenCourse);

            Map<Integer, Integer> absenceCounts = new HashMap<>();
            for (Student student : students) {
                int count = AbsenceDAO.getAbsenceCountByStudentAndYear(
                        student.getStudentID(),
                        activationYear,
                        creds
                );
                absenceCounts.put(student.getStudentID(), count);
            }

            CommonView.showStudentsAbsences(students, absenceCounts);

        } catch (DataAccessException e) {
            CommonView.showMessage("Errore di accesso ai dati: " + e.getMessage());
        } catch (IOException e) {
            CommonView.showMessage("Errore di input/output: " + e.getMessage());
        } catch (Exception e) {
            CommonView.showMessage("Si è verificato un errore inatteso: " + e.getMessage());
        }
    }
    //CHECKED
    protected abstract List<Student> loadStudentsForCourse(Course course) throws DataAccessException;



}
