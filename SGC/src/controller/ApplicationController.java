package controller;

import model.dao.LoginUserDAO;
import model.domain.Credentials;
import view.LoginView;

public class ApplicationController {

    public void start() {
        try {
            // Prendo username/password/ID dalla view
            Credentials creds = LoginView.authenticate();

            // Controllo le credenziali col LoginController
            LoginController loginController = new LoginController();
            Credentials verified = loginController.login(
                    creds.getUsername(),
                    creds.getPassword(),
                    creds.getID()
            );

            LoginView.showLoginSuccess(verified.getRole().name());

            //creo il controller corretto in base al ruolo
            UserController controller = createController(verified);

            if(controller != null) {
                controller.start();
            } else {
                System.out.println("Ruolo non riconosciuto o credenziali inadeguate per l'accesso.");
            }

        } catch (Exception e) {
            LoginView.showLoginError(e.getMessage());
        }
    }

    private UserController createController (Credentials verified) {
        return switch (verified.getRole()) {
            case AMMINISTRATIVO -> new AdministrativeController(verified);
            case INSEGNANTE -> new TeacherController(verified);
            default -> null;
        };
    }


}

