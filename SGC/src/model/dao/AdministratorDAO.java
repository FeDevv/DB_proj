package model.dao;

import exception.DataAccessException;
import model.domain.Administrator;
import model.domain.Credentials;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
}
