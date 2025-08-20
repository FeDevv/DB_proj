package model.dao;

import exception.DataAccessException;
import model.domain.Assignment;
import model.domain.Credentials;
import model.domain.Lesson;
import model.domain.Teacher;

import java.sql.*;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class AssignmentDAO {

    public static void checkScheduleConflicts(List<Teacher> teachers, List<Lesson> lessons, Credentials creds)
            throws SQLException, DataAccessException {

        String sql = """
        SELECT COUNT(*) AS count
        FROM assignment a
        JOIN course c ON a.course_id = c.id AND a.level_name = c.level_name
        JOIN lessons l ON a.course_id = l.course_id AND a.level_name = l.level_name
        WHERE a.teacher_id = ?
          AND l.day_of_week = ?
          AND (
            (l.start_time < ? AND l.end_time > ?) OR
            (l.start_time BETWEEN ? AND ?) OR
            (l.end_time BETWEEN ? AND ?)
          )
        """;

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Teacher teacher : teachers) {
                for (Lesson lesson : lessons) {
                    LocalTime start = lesson.getStartTime();
                    LocalTime end = lesson.getEndTime();

                    stmt.setInt(1, teacher.getTeacherID());
                    stmt.setString(2, lesson.getDayOfWeek().toString());
                    stmt.setTime(3, Time.valueOf(end));     // start_time < end_time_new
                    stmt.setTime(4, Time.valueOf(start));   // end_time > start_time_new
                    stmt.setTime(5, Time.valueOf(start));   // BETWEEN start
                    stmt.setTime(6, Time.valueOf(end));     // BETWEEN end
                    stmt.setTime(7, Time.valueOf(start));   // BETWEEN start
                    stmt.setTime(8, Time.valueOf(end));     // BETWEEN end

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next() && rs.getInt("count") > 0) {
                            throw new DataAccessException(
                                    "Conflitto orario per " + teacher.getName() +
                                            " il " + lesson.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ITALIAN) +
                                            " tra " + lesson.getFormattedTime()
                            );
                        }
                    }
                }
            }
        }
    }

    public static void insertAssignments(List<Assignment> assignments, Credentials creds) throws DataAccessException {
        String sql = "INSERT INTO assignment (teacher_id, admin_ID, course_code, level_name, start_hour, start_minute, end_hour, end_minute) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Assignment a : assignments) {
                stmt.setInt(1, a.getTeacherId());
                stmt.setInt(2, creds.getID());
                stmt.setInt(3, a.getCourseCode());
                stmt.setString(4, a.getLevelName().name()); // se LevelName è enum
                stmt.setInt(5, a.getStartTime().getHour());
                stmt.setInt(6, a.getStartTime().getMinute());
                stmt.setInt(7, a.getEndTime().getHour());
                stmt.setInt(8, a.getEndTime().getMinute());
                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'inserimento assegnazioni: " + e.getMessage(), e);
        }
    }

    public static void deleteAssignment(Lesson lesson, Credentials creds) throws DataAccessException {
        String sql = "DELETE FROM assignments " +
                "WHERE course_code = ? AND level_name = ? " +
                "AND start_hour = ? AND start_minute = ? " +
                "AND end_hour = ? AND end_minute = ?";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lesson.getProgressiveCode()); // course_code == progressiveCode del corso
            stmt.setString(2, lesson.getLevel().name());
            stmt.setInt(3, lesson.getStartTime().getHour());
            stmt.setInt(4, lesson.getStartTime().getMinute());
            stmt.setInt(5, lesson.getEndTime().getHour());
            stmt.setInt(6, lesson.getEndTime().getMinute());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Nessun assignment trovato per la lezione specificata.");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore durante l'eliminazione dell'assignment: " + e.getMessage(), e);
        }
    }

}
