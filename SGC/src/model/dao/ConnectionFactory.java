package model.dao;

import model.domain.Credentials;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionFactory {
    private static Properties properties;

    static {
        try (InputStream input = ConnectionFactory.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("File db.properties non trovato.");
            }
            properties = new Properties();
            properties.load(input);
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (Exception e) {
            throw new RuntimeException("Errore caricando configurazione DB", e);
        }
    }

    public static Connection getConnection(Credentials credentials) throws SQLException {
        String url = properties.getProperty("CONNECTION_URL");

        String userKey = credentials.getRole().name() + "_USER";
        String passKey = credentials.getRole().name() + "_PASS";

        String user = properties.getProperty(userKey);
        String pass = properties.getProperty(passKey);

        if (user == null || pass == null) {
            throw new IllegalArgumentException("Credenziali non trovate per ruolo: " + credentials.getRole());
        }

        return DriverManager.getConnection(url, user, pass);
    }
}

