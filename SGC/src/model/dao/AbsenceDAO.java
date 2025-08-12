package model.dao;

import exception.DataAccessException;
import model.domain.Absence;
import model.domain.Credentials;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AbsenceDAO {
    public void recordAbsence(Absence absence, Credentials creds) throws DataAccessException {
        String sql = "INSERT INTO absences (student_id, absence_day, absence_month, absence_year) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, absence.getStudentId());
            stmt.setInt(2, absence.getDate().getDayOfMonth());
            stmt.setInt(3, absence.getDate().getMonthValue());
            stmt.setInt(4, absence.getDate().getYear());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Registrazione assenza fallita");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel salvataggio assenza: " + e.getMessage(), e);
        }
    }

    public List<Absence> getAbsencesByStudent(int studentId, Credentials creds) throws DataAccessException {
        String sql = "SELECT * FROM absences WHERE student_id = ? ORDER BY absence_year DESC, absence_month DESC, absence_day DESC";
        List<Absence> absences = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = LocalDate.of(
                            rs.getInt("absence_year"),
                            rs.getInt("absence_month"),
                            rs.getInt("absence_day")
                    );
                    absences.add(new Absence(
                            studentId,
                            date
                    ));
                }
            }
            return absences;

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero assenze: " + e.getMessage(), e);
        }
    }


}