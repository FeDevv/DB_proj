package model.dao;

import exception.DataAccessException;
import model.domain.Credentials;
import model.domain.LevelName;
import model.domain.Student;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {
    public void createStudent(Student student, Credentials creds) throws DataAccessException {
        String sql = "INSERT INTO students (name, last_name, cf, birth_place, telephone, " +
                "birth_date, city, cap, street, street_number) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Imposta i parametri senza studentID
            stmt.setString(1, student.getName());
            stmt.setString(2, student.getLastName());
            stmt.setString(3, student.getCf());
            stmt.setString(4, student.getBirthPlace());
            stmt.setString(5, student.getTelephoneNumber());
            stmt.setDate(6, java.sql.Date.valueOf(student.getBirthDate()));
            stmt.setString(7, student.getCity());
            stmt.setString(8, student.getCap());
            stmt.setString(9, student.getStreet());
            stmt.setInt(10, student.getStreetNumber());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new DataAccessException("Inserimento studente fallito, nessuna riga modificata.");
            }

            // Recupera l'ID generato
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    student.setStudentID(newId);  // Aggiorna l'oggetto Student
                } else {
                    throw new DataAccessException("Inserimento studente fallito, nessun ID ottenuto.");
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore nell'inserimento studente: " + e.getMessage(), e);
        }
    }

    public List<Student> getStudentsByCourse(int progressiveCode,
                                             LevelName level,
                                             Integer teacherId,
                                             Credentials creds) throws DataAccessException {

        String sqlAll =
                "SELECT s.studentID, s.name, s.lastName, s.cf, s.birthPlace, " +
                        " s.telephoneNumber, s.birthDate, s.city, s.cap, s.street, s.streetNumber " +
                        "FROM studente s " +
                        "JOIN iscrizione i ON s.studentID = i.studentID " +
                        "JOIN corso c ON c.nome_livello = i.nome_livello AND c.codice_progressivo = i.codice_progressivo " +
                        "WHERE i.codice_progressivo = ? AND i.nome_livello = ? " +
                        "ORDER BY s.lastName, s.name";

        String sqlTeacherFiltered =
                "SELECT s.studentID, s.name, s.lastName, s.cf, s.birthPlace, " +
                        " s.telephoneNumber, s.birthDate, s.city, s.cap, s.street, s.streetNumber " +
                        "FROM studente s " +
                        "JOIN iscrizione i ON s.studentID = i.studentID " +
                        "JOIN corso c ON c.nome_livello = i.nome_livello AND c.codice_progressivo = i.codice_progressivo " +
                        "JOIN assegnazione a ON a.nome_livello = c.nome_livello AND a.codice_progressivo = c.codice_progressivo " +
                        "JOIN insegnante t ON t.insegnanteID = a.insegnanteID " +
                        "WHERE i.codice_progressivo = ? AND i.nome_livello = ? AND t.insegnanteID = ? " +
                        "ORDER BY s.lastName, s.name";

        String sql = (teacherId == null) ? sqlAll : sqlTeacherFiltered;

        List<Student> students = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, progressiveCode);
            stmt.setString(2, level.name()); // usa il nome esatto dell'enum come memorizzato nel DB

            if (teacherId != null) {
                stmt.setInt(3, teacherId);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // birthDate può essere null nel DB -> gestirlo
                    Date bd = rs.getDate("birthDate");
                    LocalDate birthDate = (bd != null) ? bd.toLocalDate() : null;

                    Student student = new Student(
                            rs.getString("name"),
                            rs.getString("lastName"),
                            rs.getString("cf"),
                            rs.getString("birthPlace"),
                            rs.getString("telephoneNumber"),
                            birthDate,
                            rs.getString("city"),
                            rs.getString("cap"),
                            rs.getString("street"),
                            rs.getInt("streetNumber")
                    );

                    student.setStudentID(rs.getInt("studentID"));

                    students.add(student);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero studenti per corso", e);
        } catch (Exception e) {
            throw new DataAccessException("Errore generico durante l'accesso ai dati", e);
        }

        return students;
    }

    public Student getStudentByID(int ID, Credentials creds) throws DataAccessException {
        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, ID);
            Student student;
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    student = new Student(rs.getString("name"),
                            rs.getString("last_name"),
                            rs.getString("cf"),
                            rs.getString("birth_place"),
                            rs.getString("telephone"),
                            rs.getDate("birth_date").toLocalDate(),
                            rs.getString("city"),
                            rs.getString("cap"),
                            rs.getString("street"),
                            rs.getInt("street_number")
                    );
                    student.setStudentID(rs.getInt("student_id"));

                    return student;
                }
            }
            return null;

        } catch (SQLException e) {
            throw new DataAccessException("Errore nel recupero studente: " + e.getMessage(), e);
        }
    }

    public void deleteStudent(int studentID, Credentials creds) throws DataAccessException {
        String sql = "DELETE FROM students WHERE studentID = ?";

        try (Connection conn = ConnectionFactory.getConnection(creds);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentID);
            int affectedRows = stmt.executeUpdate();

            // Controlliamo il risultato dell'operazione
            if (affectedRows == 0) {
                throw new DataAccessException("Impossibile eliminare lo studente con ID: " + studentID);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Errore durante la cancellazione dello studente: " + e.getMessage(), e);
        }
    }

}
