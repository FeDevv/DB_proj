package view;

import controller.LoginController;
import model.domain.Credentials;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoginView {

    private LoginView() {} // Evita istanziazione

    public static Credentials authenticate() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.print("username >> ");
        String username = reader.readLine();

        System.out.print("ID >> ");
        String ID = reader.readLine();

        System.out.print("password >> ");
        String password = reader.readLine();

        // Il ruolo resta null: sarà il controller a riempirlo
        return new Credentials(username, password, null, Integer.parseInt(ID));
    }

    public static void showLoginSuccess(String ruolo) {
        System.out.println("Login effettuato con successo! Ruolo: " + ruolo);
    }

    public static void showLoginError(String message) {
        System.out.println("Errore login: " + message);
    }
}
