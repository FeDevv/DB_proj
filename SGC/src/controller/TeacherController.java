package controller;

import exception.DataAccessException;
import model.dao.CourseDAO;
import model.dao.ReportDAO;
import model.domain.Course;
import model.domain.Credentials;
import model.domain.Student;
import model.domain.WeeklyReport;
import view.CommonView;
import view.TeacherView;

import java.sql.SQLException;
import java.util.List;

import static view.CommonView.askCourseSelection;
import static view.CommonView.showStudentsList;

public class TeacherController extends UserController{

    public TeacherController(Credentials creds) {
        super(creds);
    }

    @Override
    protected void showWelcomeMessage(){
        TeacherView.showWelcomeMessage(creds.getUsername());
    }

    @Override
    protected int showMenu() {
        return TeacherView.showMenu();
    }

    @Override
    protected boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1 -> {
                printStudentsFromCourse();
            }
            case 2 -> {
                showCourses();
            }
            case 3 -> {
                recordStudentAbsence();
            }
            case 4 -> {
                generateWeeklyReport();
            }
            case 5 -> CommonView.showMessage("Storico assenze...");
            case 0 -> { return false; } // Logout
            default -> TeacherView.showInvalidOption();
        }
        return true;
    }

    @Override
    protected void printStudentsFromCourse() {
        Course course = askCourseSelection();
        if(course == null) {
            CommonView.showMessage("Selezione corso non valida");
        } else {
            List<Student> students = fetchStudentsForCourse(course.getCourseID(), course.getLevel());
            showStudentsList(students);
        }
    }

    protected void showCourses() {
        try {
            CourseDAO assignedCoursesDAO = new CourseDAO();
            List<Course> courses = CourseDAO.getCoursesByTeacher(creds.getID(), creds);

            if (courses == null || courses.isEmpty()) {
                TeacherView.showNoCoursesMessage(creds.getUsername());
            } else {
                TeacherView.showAssignedCourses(courses, creds.getUsername());
            }

        } catch (DataAccessException e) {
            // Distingue tra diversi tipi di errori
            if (e.getCause() instanceof SQLException) {
                SQLException sqlExc = (SQLException) e.getCause();
                CommonView.showMessage("Errore database [" + sqlExc.getSQLState() + "]: " + e.getMessage());
            } else {
                CommonView.showMessage("Errore nel recupero corsi: " + e.getMessage());
            }

            // Log dettagliato per il debug
            System.err.println("Errore dettagliato: ");
            e.printStackTrace();
        }
    }

    private void generateWeeklyReport() {
        try {
            // Crea il payload del report
            String reportData = TeacherView.generateWeeklyReport(creds.getID());

            // Crea oggetto report per la settimana corrente
            WeeklyReport report = WeeklyReport.createForCurrentWeek(reportData);

            // Inserisci nel database
            int reportID = ReportDAO.insertWeeklyReport(report, creds);

            CommonView.showMessage("Report settimanale creato con ID: " + reportID);

        } catch (DataAccessException e) {
            CommonView.showMessage("Errore di database: " + e.getMessage());
        } catch (Exception e) {
            CommonView.showMessage("Errore imprevisto: " + e.getMessage());
        }
    }

    @Override
    protected void showExitMessage() {
        CommonView.showExitMessage(creds.getUsername());
    }
}
