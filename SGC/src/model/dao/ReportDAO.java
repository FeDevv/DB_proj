package model.dao;


import exception.DataAccessException;
import model.domain.Credentials;
import model.domain.MonthlyReport;
import model.domain.WeeklyReport;

import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

public class ReportDAO {

    public static int insertMonthlyReport(MonthlyReport report, int adminID, List<Integer> teacherIDs, Credentials credentials)
            throws DataAccessException {

        Connection conn = null;
        PreparedStatement reportStmt = null;
        PreparedStatement regardStmt = null;
        ResultSet generatedKeys = null;

        if (hasMonthlyReportThisMonth(credentials)) {
            throw new DataAccessException(
                    "Esiste già un report mensile per questo mese."
            );
        }

        try {
            conn = ConnectionFactory.getConnection(credentials);
            conn.setAutoCommit(false); // Inizia transazione

            // 1. Inserimento del report principale
            String reportSql = "INSERT INTO monthlyReport (adminID, date, data) VALUES (?, ?, ?)";
            reportStmt = conn.prepareStatement(reportSql, Statement.RETURN_GENERATED_KEYS);

            reportStmt.setInt(1, adminID);
            reportStmt.setDate(2, Date.valueOf(report.getDate()));
            reportStmt.setString(3, report.getData());

            int affectedRows = reportStmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Inserimento report fallito, nessuna riga modificata.");
            }

            // 2. Recupero ID generato
            generatedKeys = reportStmt.getGeneratedKeys();
            int reportID;
            if (generatedKeys.next()) {
                reportID = generatedKeys.getInt(1);
            } else {
                throw new DataAccessException("Inserimento report fallito, nessun ID ottenuto.");
            }

            // 3. Inserimento associazioni insegnanti
            String regardSql = "INSERT INTO regard (teacherID, monthlyReportID) VALUES (?, ?)";
            regardStmt = conn.prepareStatement(regardSql);

            for (int teacherID : teacherIDs) {
                regardStmt.setInt(1, teacherID);
                regardStmt.setInt(2, reportID);
                regardStmt.addBatch();
            }

            int[] batchResults = regardStmt.executeBatch();
            for (int result : batchResults) {
                if (result == PreparedStatement.EXECUTE_FAILED) {
                    throw new DataAccessException("Inserimento associazione insegnante-report fallito");
                }
            }

            conn.commit(); // Commit transazione
            return reportID;

        } catch (SQLException e) {
            throw new DataAccessException("Errore database durante l'inserimento del report", e);
        }
    }

    public static boolean hasMonthlyReportThisMonth(Credentials credentials) throws DataAccessException {
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        String sql = """
        SELECT COUNT(*) 
        FROM monthlyReport 
        WHERE month = ? AND year = ?
    """;

        try (Connection conn = ConnectionFactory.getConnection(credentials);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, currentMonth);
            pstmt.setInt(2, currentYear);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true se già esiste almeno 1 report per il mese
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore durante il controllo del report mensile", e);
        }

        return false;
    }


    public static int insertWeeklyReport(WeeklyReport report, Credentials credentials) throws DataAccessException {

        if (hasWeeklyReportThisWeek(credentials)) {
            throw new DataAccessException(
                    "Esiste già un report settimanale per questa settimana per l'insegnante con ID " + credentials.getID()
            );
        }


        String sql = "INSERT INTO weeklyReport (weekNumber, year, reportDate, data) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(credentials);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, report.getWeekNumber());
            pstmt.setInt(2, report.getYear());
            pstmt.setDate(3, Date.valueOf(report.getReportDate()));
            pstmt.setString(4, report.getData());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("Inserimento fallito, nessuna riga modificata.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new DataAccessException("Inserimento fallito, nessun ID ottenuto.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore durante l'inserimento del report settimanale", e);
        }
    }

    public static boolean hasWeeklyReportThisWeek(Credentials credentials) throws DataAccessException {
        // 1. Recupera settimana e anno correnti
        LocalDate today = LocalDate.now();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeek = today.get(weekFields.weekOfWeekBasedYear());
        int currentYear = today.getYear();

        String sql = """
        SELECT COUNT(*) 
        FROM weeklyReport 
        WHERE teacherID = ? 
          AND weekNumber = ? 
          AND year = ?
    """;

        try (Connection conn = ConnectionFactory.getConnection(credentials);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, credentials.getID());
            pstmt.setInt(2, currentWeek);
            pstmt.setInt(3, currentYear);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // true se esiste almeno un report
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel controllo report settimanale", e);
        }

        return false;
    }



}