package model.dao;

import exception.DataAccessException;
import model.domain.Credentials;
import model.domain.LevelName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class EnrollmentDAO {
    //funzione per iscrivere uno studente
    public void enrollStudent(int studentId, int courseId, LevelName level, Credentials creds)
            throws DataAccessException {
        int adminID = creds.getID();
        String sql = "INSERT INTO enrollments (student_id, admin_ID, course_id, level_name, enrollment_date) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, adminID);
            stmt.setInt(3, courseId);
            stmt.setString(4, level.name());
            stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Iscrizione fallita, nessuna riga modificata.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'iscrizione studente al corso: " + e.getMessage(), e);
        }
    }

    //funzione per controllare se uno studente è gia iscritto
    public boolean isStudentEnrolled(int studentId, int courseId, LevelName level, Credentials creds)
            throws DataAccessException {
        String sql = "SELECT COUNT(*) FROM enrollments " +
                "WHERE student_id = ? AND course_id = ? AND level_name = ?";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            stmt.setInt(2, courseId);
            stmt.setString(3, level.name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            return false;

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel controllo iscrizione: " + e.getMessage(), e);
        }
    }
}

