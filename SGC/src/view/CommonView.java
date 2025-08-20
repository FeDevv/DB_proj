package view;

import model.Utils.DateUtils;
import model.domain.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

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

    public static Absence inputAbsenceDetails() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\n--- REGISTRAZIONE ASSENZA ---");

        System.out.print("ID Studente: ");
        int studentId = Integer.parseInt(reader.readLine().trim());

        LocalDate date = null;
        while (date == null) {
            try {
                System.out.print("Data assenza (gg/mm/aaaa): ");
                String[] parts = reader.readLine().trim().split("/");
                date = LocalDate.of(
                        Integer.parseInt(parts[2]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[0])
                );

                if (date.isAfter(LocalDate.now())) {
                    System.out.println("La data non può essere futura!");
                    date = null;
                }
            } catch (Exception e) {
                System.out.println("Formato data non valido!");
            }
        }

        return new Absence(studentId, date);
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

    public static int getGenericInteger() throws IOException {
        int generic;
        System.out.print(">> ");
        generic = Integer.parseInt(reader.readLine().trim());

        return generic;
    }

    public static void showStudentsAbsences(List<Student> students, Map<Integer, Integer> absenceCounts) {
        if (students == null || students.isEmpty()) {
            System.out.println("\nNessuno studente trovato nel sistema.");
            return;
        }

        System.out.println("\n=== ELENCO ASSENZE STUDENTI ===");
        System.out.println("-------------------------------------------------------------------");
        System.out.printf("%-4s %-5s %-20s %-20s %-10s%n",
                "N.", "ID", "Nome", "Cognome", "Assenze");
        System.out.println("-------------------------------------------------------------------");

        int counter = 1;
        for (Student s : students) {
            int absences = absenceCounts.getOrDefault(s.getStudentID(), 0);

            System.out.printf("%-4d %-5d %-20s %-20s %-10d%n",
                    counter++,
                    s.getStudentID(),
                    s.getName(),
                    s.getLastName(),
                    absences);
        }

        System.out.println("-------------------------------------------------------------------");
        System.out.println("Totale studenti: " + students.size());
    }

}
