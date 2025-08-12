package model.dao;

import exception.DataAccessException;
import model.domain.Credentials;
import model.domain.Lesson;
import model.domain.LevelName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class LessonDAO {

    public List<Lesson> getLessonsByCourse(int progressiveCode, LevelName level, Credentials creds)
            throws DataAccessException {

        String sql = "SELECT progressive_code, level_name, day_of_week, "
                + "start_hour, start_minute, end_hour, end_minute, classroom "
                + "FROM lessons WHERE progressive_code = ? AND level_name = ? "
                + "ORDER BY day_of_week, start_hour, start_minute";

        List<Lesson> lessons = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, progressiveCode);
            stmt.setString(2, level.name());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DayOfWeek day = DayOfWeek.valueOf(rs.getString("day_of_week"));

                    LocalTime startTime = LocalTime.of(
                            rs.getInt("start_hour"),
                            rs.getInt("start_minute")
                    );

                    LocalTime endTime = LocalTime.of(
                            rs.getInt("end_hour"),
                            rs.getInt("end_minute")
                    );

                    lessons.add(new Lesson(
                            rs.getInt("progressive_code"),
                            LevelName.valueOf(rs.getString("level_name")),
                            day,
                            startTime,
                            endTime,
                            rs.getString("classroom")
                    ));
                }
            }
            return lessons;

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero lezioni: " + e.getMessage(), e);
        }
    }

    public void deleteLesson(Lesson lesson, Credentials creds) throws DataAccessException {
        String sql = "DELETE FROM lessons WHERE progressive_code = ? AND level_name = ? "
                + "AND day_of_week = ? AND start_hour = ? AND start_minute = ?";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, lesson.getProgressiveCode());
            stmt.setString(2, lesson.getLevel().name());
            stmt.setString(3, lesson.getDayOfWeek().name());
            stmt.setInt(4, lesson.getStartTime().getHour());
            stmt.setInt(5, lesson.getStartTime().getMinute());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Lezione non trovata");
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'eliminazione lezione: " + e.getMessage(), e);
        }
    }

    public void insertLessons(List<Lesson> lessons, Credentials creds) throws DataAccessException {
        String sql = "INSERT INTO lessons (progressive_code, level_name, day_of_week, " +
                "start_hour, start_minute, end_hour, end_minute, classroom) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Lesson lesson : lessons) {
                stmt.setInt(1, lesson.getProgressiveCode());
                stmt.setString(2, lesson.getLevel().name());
                stmt.setString(3, lesson.getDayOfWeek().name());
                stmt.setInt(4, lesson.getStartTime().getHour());
                stmt.setInt(5, lesson.getStartTime().getMinute());
                stmt.setInt(6, lesson.getEndTime().getHour());
                stmt.setInt(7, lesson.getEndTime().getMinute());
                stmt.setString(8, lesson.getClassroom());
                stmt.addBatch();
            }

            stmt.executeBatch();

        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'inserimento lezioni: " + e.getMessage(), e);
        }
    }

    public boolean hasOverlappingLesson(Lesson newLesson, Credentials creds) throws DataAccessException {
        String sql = "SELECT 1 FROM lessons l " +
                "JOIN courses c ON l.progressive_code = c.courseID AND l.level_name = c.levelName " +
                "WHERE c.active = TRUE " +  // Solo corsi attivi
                "AND l.classroom = ? " +
                "AND l.day_of_week = ? " +
                "AND l.start_hour * 60 + l.start_minute < ? " +
                "AND l.end_hour * 60 + l.end_minute > ? " +
                "LIMIT 1";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            LocalTime newStart = newLesson.getStartTime();
            LocalTime newEnd = newLesson.getEndTime();

            int startMinuteOfDay = newStart.getHour() * 60 + newStart.getMinute();
            int endMinuteOfDay = newEnd.getHour() * 60 + newEnd.getMinute();

            stmt.setString(1, newLesson.getClassroom());
            stmt.setString(2, newLesson.getDayOfWeek().name());
            stmt.setInt(3, endMinuteOfDay);  // Fine della nuova lezione
            stmt.setInt(4, startMinuteOfDay); // Inizio della nuova lezione

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel controllo sovrapposizione lezioni: " + e.getMessage(), e);
        }
    }
}

