package controller;

import exception.ApplicationException;
import exception.DataAccessException;
import model.dao.StudentDAO;
import model.domain.Credentials;
import model.domain.LevelName;
import model.domain.Role;
import model.domain.Student;
import view.CommonView;

import java.sql.SQLException;
import java.util.Collections;
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


    protected void registraAssenzaStudente() {

        System.out.println("Assenza registrata");
    }

}
