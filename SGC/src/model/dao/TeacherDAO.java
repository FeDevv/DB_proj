package model.dao;

import exception.DataAccessException;
import model.domain.Credentials;
import model.domain.Teacher;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TeacherDAO {

    public List<Teacher> getAllTeachers(Credentials creds) throws DataAccessException {
        String sql = "SELECT * FROM teachers ORDER BY lastName, name";
        List<Teacher> teachers = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection(creds);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                teachers.add(new Teacher(
                        rs.getInt("teacherID"),
                        rs.getString("name"),
                        rs.getString("lastName"),
                        rs.getString("nation"),
                        rs.getBoolean("active"),
                        rs.getString("city"),
                        rs.getString("cap"),
                        rs.getString("street"),
                        rs.getInt("streetNumber")
                ));
            }
            return teachers;

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero insegnanti: " + e.getMessage(), e);
        }
    }
}
