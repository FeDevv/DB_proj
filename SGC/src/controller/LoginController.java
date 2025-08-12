package controller;

import model.dao.LoginUser;
import model.dao.LoginUserDAO;
import model.domain.Credentials;
import model.domain.Role;
import view.LoginView;

import java.io.IOException;

public class LoginController {
    private LoginUserDAO loginUserDAO;

    public LoginController() {
        this.loginUserDAO = new LoginUser(); // la tua implementazione
    }

    public Credentials login(String username, String password, int ID) throws Exception {
        Role ruolo = loginUserDAO.verificaCredenziali(username, password, ID);
        if (ruolo != null) {
            return new Credentials(username, password, ruolo, ID);
        } else {
            throw new Exception("Credenziali non valide.");
        }
    }
}

