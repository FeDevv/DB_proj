package model.dao;

import exception.DataAccessException;
import model.domain.Credentials;
import model.domain.Teacher;

import java.sql.*;
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
                Teacher teacher = new Teacher(
                        rs.getString("name"),
                        rs.getString("lastName"),
                        rs.getString("nation"),
                        rs.getBoolean("active"),
                        rs.getString("city"),
                        rs.getString("cap"),
                        rs.getString("street"),
                        rs.getInt("streetNumber")
                );

                teacher.setTeacherID(rs.getInt("teacherID"));

                teachers.add(teacher);
            }
            return teachers;

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero insegnanti: " + e.getMessage(), e);
        }
    }

    //inserisce un insegnante, ritorna l'ID dell insegnante creato
    public int insertTeacher(Teacher teacher, Credentials creds) throws DataAccessException {
        String sql = "INSERT INTO teachers (name, lastName, nation, active, city, cap, street, streetNumber) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Imposta i parametri della query
            pstmt.setString(1, teacher.getName());
            pstmt.setString(2, teacher.getLastName());
            pstmt.setString(3, teacher.getNation());
            pstmt.setBoolean(4, teacher.isActive());
            pstmt.setString(5, teacher.getCity());
            pstmt.setString(6, teacher.getCap());
            pstmt.setString(7, teacher.getStreet());
            pstmt.setInt(8, teacher.getStreetNumber());

            // Esegui l'inserimento
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Inserimento fallito, nessuna riga modificata.");
            }

            // Recupera l'ID generato
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DataAccessException("Inserimento fallito, nessun ID ottenuto.");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore durante l'inserimento dell'insegnante", e);
        } catch (RuntimeException e) {
            throw new DataAccessException("Errore di configurazione del database", e);
        }
    }

}
