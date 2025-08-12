package model.dao;

import model.domain.Credentials;
import model.domain.Role;
import view.LoginView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginUser implements LoginUserDAO {
    @Override
    public Role verificaCredenziali(String username, String password, int ID) {
        String sql = "SELECT ruolo FROM utenti WHERE username = ? AND password = ? AND ID = ?";

        try (Connection conn = ConnectionFactory.getConnection(
                new Credentials("login", "login", Role.ALTRO, 0) // ruoli minimi
        );
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password); // ⚠️ in produzione dovresti hashare
            stmt.setInt(3, ID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Role.valueOf(rs.getString("ruolo"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // credenziali non valide
    }
}
