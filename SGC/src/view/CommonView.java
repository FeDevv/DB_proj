package view;

import model.domain.Course;
import model.domain.LevelName;
import model.domain.Student;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CommonView {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void showExitMessage(String username) {
        System.out.println("Logout eseguito.");
        System.out.print("Arrivederci, " + username + "!");
    }

    public static Course askCourseSelection() {

        try {
            System.out.print("Inserisci codice progressivo del corso: ");
            int code = Integer.parseInt(reader.readLine().trim());

            // Mostra i livelli disponibili
            LevelName[] levels = LevelName.values();
            for (int i = 0; i < levels.length; i++) {
                System.out.println((i + 1) + ". " + levels[i].name());
            }
            System.out.print("Scelta livello (numero) >> ");
            int choice = Integer.parseInt(reader.readLine().trim());
            if (choice < 1 || choice > levels.length) {
                showMessage("Scelta livello non valida.");
                return null;
            }
            LevelName level = levels[choice - 1];
            Course newCourse = new Course(level, null, false);
            newCourse.setCourseID(code);
            return newCourse;
        } catch (Exception e) {
            showMessage("Input non valido.");
            return null;
        }

    }

    public static void showStudentsList(List<Student> students) {
        if (students == null || students.isEmpty()) {
            showMessage("Nessuno studente trovato.");
            return;
        }
        System.out.println("\n--- Elenco studenti ---");
        for (Student s : students) {
            System.out.printf("%d - %s %s (CF: %s)%n",
                    s.getStudentID(),
                    s.getName(),
                    s.getLastName(),
                    s.getCf());
        }
    }

    public static void showMessage(String message) {
        System.out.println(message);
    }

    public static String getGenericString() throws IOException {
        String generic;
        System.out.print(">> ");
        generic = reader.readLine();

        return generic;
    }

}
