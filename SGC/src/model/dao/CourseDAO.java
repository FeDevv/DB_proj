package model.dao;

import exception.DataAccessException;
import model.Utils.DateUtils;
import model.domain.Course;
import model.domain.Credentials;
import model.domain.LevelName;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CourseDAO {
    //inserisci un nuovo corso
    public void insertCourse(Course course, Credentials creds) throws DataAccessException {
        String sql = "INSERT INTO courses (levelName, start_day, start_month, start_year, active) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Imposta i parametri senza courseID
            stmt.setString(1, course.getLevel().name());

            // Gestione data di attivazione (potrebbe essere null)
            if (course.getActivationDate() != null) {
                stmt.setInt(2, course.getActivationDate().getDayOfMonth());
                stmt.setInt(3, course.getActivationDate().getMonthValue());
                stmt.setInt(4, course.getActivationDate().getYear());
            } else {
                stmt.setNull(2, Types.INTEGER);
                stmt.setNull(3, Types.INTEGER);
                stmt.setNull(4, Types.INTEGER);
            }

            stmt.setBoolean(5, course.isActive());

            // Esegui l'inserimento
            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Inserimento corso fallito, nessuna riga modificata.");
            }

            // Recupera l'ID generato
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    course.setCourseID(newId);  // Aggiorna l'oggetto Course
                } else {
                    throw new DataAccessException("Inserimento corso fallito, nessun ID ottenuto.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'inserimento corso: " + e.getMessage(), e);
        }
    }

    //prendi i corsi legati a un insegnante
    public static List<Course> getCoursesByTeacher(int teacherId, Credentials creds) throws DataAccessException {
        String sql = "SELECT c.courseID, c.levelName, c.start_day, c.start_month, c.start_year, c.active " +
                "FROM courses c " +
                "JOIN assignments a ON c.courseID = a.courseID " +
                "WHERE a.teacherID = ? " +
                "ORDER BY c.start_year DESC, c.start_month DESC, c.start_day DESC";

        List<Course> courses = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, teacherId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate startDate = null;

                    // Recupera i componenti della data solo se non sono NULL
                    if (rs.getObject("start_day") != null &&
                            rs.getObject("start_month") != null &&
                            rs.getObject("start_year") != null) {

                        int day = rs.getInt("start_day");
                        int month = rs.getInt("start_month");
                        int year = rs.getInt("start_year");

                        try {
                            startDate = DateUtils.toLocalDate(day, month, year);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Data non valida nel DB: " + e.getMessage());
                        }
                    }

                    Course newCourse = new Course(LevelName.valueOf(rs.getString("levelName")),
                            startDate,
                            rs.getBoolean("active"));

                    newCourse.setCourseID(rs.getInt("courseID"));

                    courses.add(newCourse);
                }
            }
            return courses;

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero corsi per insegnante: " + e.getMessage(), e);
        }
    }

    //prendi tutti i corsi attivi
    public List<Course> getActiveCourses(Credentials creds) throws DataAccessException {
        String sql = "SELECT * FROM courses WHERE active = TRUE";
        List<Course> courses = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection(creds);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int day = rs.getInt("start_day");
                int month = rs.getInt("start_month");
                int year = rs.getInt("start_year");
                LocalDate startDate = (rs.wasNull()) ? null : LocalDate.of(year, month, day);

                Course newCourse = new Course(
                        LevelName.valueOf(rs.getString("levelName")),
                        startDate,
                        rs.getBoolean("active")
                );

                newCourse.setCourseID(rs.getInt("CourseID"));

                courses.add(newCourse);
            }
            return courses;

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero corsi attivi", e);
        }
    }
}


