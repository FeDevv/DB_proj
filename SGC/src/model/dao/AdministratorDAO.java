package model.dao;

import exception.DataAccessException;
import model.domain.Administrator;
import model.domain.Credentials;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdministratorDAO {

    public List<Administrator> getAllAdministrators(Credentials creds) throws DataAccessException {
        String sql = "SELECT * FROM administrators ORDER BY lastName, name";
        List<Administrator> admins = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection(creds);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                admins.add(new Administrator(
                        rs.getInt("administratorID"),
                        rs.getString("email"),
                        rs.getString("name"),
                        rs.getString("lastName"),
                        rs.getString("role"),
                        rs.getBoolean("active")
                ));
            }
            return admins;

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero amministratori: " + e.getMessage(), e);
        }
    }

    public Administrator getAdministratorById(int adminID, Credentials creds) throws DataAccessException {
        String sql = "SELECT * FROM administrators WHERE adminID = ?";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, adminID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Administrator(
                            rs.getInt("administratorID"),
                            rs.getString("email"),
                            rs.getString("name"),
                            rs.getString("lastName"),
                            rs.getString("role"),
                            rs.getBoolean("active")
                    );
                };
                return null;
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore durante il recupero dell'amministratore", e);
        }
    }

    public int getAdministratorCount(Credentials creds) throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM administrators";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;

        } catch (SQLException e) {
            throw new DataAccessException("Errore durante il conteggio degli amministratori", e);
        }
    }

    public void deleteAdministrator(int adminID, Credentials creds) throws DataAccessException {
        String sql = "DELETE FROM administrators WHERE adminID = ?";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, adminID);
            int affectedRows = stmt.executeUpdate();

            // Controlliamo il risultato dell'operazione
            if (affectedRows == 0) {
                throw new DataAccessException("Impossibile eliminare l'amministratore con ID: " + adminID);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore durante la cancellazione dell'amministratore: " + e.getMessage(), e);
        }
    }
}
