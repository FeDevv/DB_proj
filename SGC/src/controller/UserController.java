package controller;

import exception.ApplicationException;
import exception.DataAccessException;
import model.dao.AbsenceDAO;
import model.dao.CourseDAO;
import model.dao.EnrollmentDAO;
import model.dao.StudentDAO;
import model.domain.*;
import view.CommonView;

import java.util.List;


public abstract class UserController {

    protected Credentials creds;

    public UserController(Credentials creds) {
        this.creds = creds;
    }

    //metodo principale comune a tutti i ruoli
    public void start() {
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
    protected abstract boolean handleMenuChoice(int choice);
    protected abstract void showExitMessage();
    protected abstract void printStudentsFromCourse();

    /*qua vanno tutte le funzioni comuni*/
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

    protected void recordStudentAbsence() {
        try {
            // Input dati assenza
            Absence absence = CommonView.inputAbsenceDetails();

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

    protected void registraAssenzaStudente() {

        System.out.println("Assenza registrata");
    }

}
