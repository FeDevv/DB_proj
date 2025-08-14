package view;

import model.domain.*;
import model.Utils.DateUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AdministrativeView {

    public static void showWelcomeMessage(String username) {
        System.out.println("Accesso come personale Amministrativo");
        System.out.println("Benvenuto, " + username);
    }

    public static int showMenu() {
        System.out.println("\n--- MENU AMMINISTRATIVO ---");
        System.out.println("1. ST1 - Stampa elenco studenti del corso");
        System.out.println("2. ST2 - Stampa elenco insegnanti");
        System.out.println("3. ST3 - Stampa personale del centro");
        System.out.println("4. VC1 - Visualizza corsi per insegnante");
        System.out.println("5. CR1 - Inserimento nuovo corso");
        System.out.println("6. IS1 - Iscrizione studente a corso");
        System.out.println("7. ASa1 - Registrazione assenza studente");
        System.out.println("8. IN1 - Inserimento nuovo insegnante");
        System.out.println("9. ASe1 - Assegnazione insegnante a corso");
        System.out.println("10. RP1 - Generazione report mensile");
        System.out.println("11. RP2 - Generazione report settimanale");
        System.out.println("12. VS1 - Visualizzazione assenze classe");
        System.out.println("0. Logout");
        System.out.print("Scelta >> ");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            return Integer.parseInt(br.readLine());
        } catch (IOException | NumberFormatException e) {
            return -1;
        }
    }

    public static void showTeachersList(List<Teacher> teachers) {
        if (teachers == null || teachers.isEmpty()) {
            System.out.println("\nNessun insegnante trovato nel sistema.");
            return;
        }

        System.out.println("\n=== ELENCO INSEGNANTI ===");
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.printf("%-4s %-5s %-20s %-20s %-15s %-8s %-30s%n",
                "N.", "ID", "Nome", "Cognome", "Nazionalità", "Stato", "Indirizzo");
        System.out.println("-------------------------------------------------------------------------------------------------------------");

        int counter = 1;
        for (Teacher t : teachers) {
            String stato = t.isActive() ? "Attivo" : "Non attivo";
            String indirizzo = t.getStreet() + " " + t.getStreetNumber() + ", " + t.getCap() + " " + t.getCity();

            System.out.printf("%-4d %-5d %-20s %-20s %-15s %-8s %-30s%n",
                    counter++,
                    t.getTeacherID(),
                    t.getName(),
                    t.getLastName(),
                    t.getNation(),
                    stato,
                    indirizzo);
        }
        System.out.println("-------------------------------------------------------------------------------------------------------------");
        System.out.println("Totale insegnanti: " + teachers.size());
    }

    public static void showAdministratorsList(List<Administrator> administrators) {
        if (administrators == null || administrators.isEmpty()) {
            System.out.println("\nNessun membro del personale trovato.");
            return;
        }

        System.out.println("\n=== PERSONALE DEL CENTRO ===");
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s %-25s %-20s %-20s %-15s %-10s%n",
                "ID", "Email", "Nome", "Cognome", "Ruolo", "Stato");
        System.out.println("-------------------------------------------------------------------------------------------------");

        for (Administrator a : administrators) {
            String stato = a.isActive() ? "Attivo" : "Non attivo";

            System.out.printf("%-5d %-25s %-20s %-20s %-15s %-10s%n",
                    a.getAdministratorID(),
                    a.getEmail(),
                    a.getName(),
                    a.getLastName(),
                    a.getRole(),
                    stato);
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.println("Totale membri: " + administrators.size());
    }

    public static void showTeacherCourses(List<Course> courses, int teacherId) {
        if (courses == null || courses.isEmpty()) {
            System.out.println("\nNessun corso assegnato all'insegnante ID: " + teacherId);
            return;
        }

        System.out.println("\n=== CORSI ASSEGNATI ALL'INSEGNANTE " + teacherId + " ===");
        System.out.println("----------------------------------------------------");
        System.out.printf("%-8s %-12s %-15s %-10s%n", "ID Corso", "Livello", "Data Inizio", "Stato");
        System.out.println("----------------------------------------------------");

        for (Course c : courses) {
            String startDate = DateUtils.formatDate(c.getActivationDate());
            String status = c.isActive() ? "Attivo" : "Non Attivo";

            System.out.printf("%-8d %-12s %-15s %-10s%n",
                    c.getCourseID(),
                    c.getLevel().toString(),
                    startDate,
                    status);
        }
        System.out.println("----------------------------------------------------");
        System.out.println("Totale corsi assegnati: " + courses.size());
    }

    public static Course inputCourseDetails() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\n--- INSERIMENTO NUOVO CORSO ---");

        // Input livello
        System.out.println("Livelli disponibili:");
        LevelName[] levels = LevelName.values();
        for (int i = 0; i < levels.length; i++) {
            System.out.println((i+1) + ". " + levels[i]);
        }
        System.out.print("Seleziona livello (1-" + levels.length + "): ");
        int levelChoice = Integer.parseInt(reader.readLine().trim());
        LevelName level = levels[levelChoice - 1];

        // Input data attivazione
        System.out.print("Data attivazione (gg/mm/aaaa): ");
        String[] dateParts = reader.readLine().trim().split("/");
        LocalDate activationDate = LocalDate.of(
                Integer.parseInt(dateParts[2]),
                Integer.parseInt(dateParts[1]),
                Integer.parseInt(dateParts[0])
        );

        // Stato attivo di default
        return new Course(level, activationDate, true);
    }

    public static List<Lesson> inputLessonsForCourse(LevelName level) throws IOException {
        List<Lesson> lessons = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("\nINSERIMENTO LEZIONI (lascia giorno vuoto per terminare)");

        while (true) {
            System.out.println("\nNuova lezione:");

            // Input giorno settimana
            System.out.println("Giorni disponibili:");
            DayOfWeek[] days = DayOfWeek.values();
            for (int i = 0; i < days.length; i++) {
                System.out.println((i+1) + ". " + days[i]);
            }
            System.out.print("Seleziona giorno (1-" + days.length + "): ");
            String dayInput = reader.readLine().trim();
            if(dayInput.isEmpty()) break;

            DayOfWeek day = days[Integer.parseInt(dayInput) - 1];

            // Input orario inizio
            System.out.print("Ora inizio (hh:mm): ");
            String[] startTime = reader.readLine().trim().split(":");
            LocalTime start = LocalTime.of(
                    Integer.parseInt(startTime[0]),
                    Integer.parseInt(startTime[1])
            );

            // Input orario fine
            System.out.print("Ora fine (hh:mm): ");
            String[] endTime = reader.readLine().trim().split(":");
            LocalTime end = LocalTime.of(
                    Integer.parseInt(endTime[0]),
                    Integer.parseInt(endTime[1])
            );

            // Input aula
            System.out.print("Aula: ");
            String classroom = reader.readLine().trim();

            // Usiamo un ID temporaneo (0), da aggiornare dopo
            lessons.add(new Lesson(0, level, day, start, end, classroom));
        }

        return lessons;
    }

    public static Student inputStudentDetails() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n--- INSERIMENTO NUOVO STUDENTE ---");

        System.out.print("Nome: ");
        String name = reader.readLine().trim();

        System.out.print("Cognome: ");
        String lastName = reader.readLine().trim();

        System.out.print("Codice Fiscale: ");
        String cf = reader.readLine().trim();

        System.out.print("Luogo di nascita: ");
        String birthPlace = reader.readLine().trim();

        System.out.print("Telefono: ");
        String telephone = reader.readLine().trim();

        System.out.print("Data di nascita (gg/mm/aaaa): ");
        String[] birthDateParts = reader.readLine().trim().split("/");
        LocalDate birthDate = LocalDate.of(
                Integer.parseInt(birthDateParts[2]),
                Integer.parseInt(birthDateParts[1]),
                Integer.parseInt(birthDateParts[0])
        );

        System.out.println("\n--- INDIRIZZO ---");
        System.out.print("Città: ");
        String city = reader.readLine().trim();

        System.out.print("CAP: ");
        String cap = reader.readLine().trim();

        System.out.print("Via: ");
        String street = reader.readLine().trim();

        System.out.print("Numero civico: ");
        int streetNumber = Integer.parseInt(reader.readLine().trim());

        return new Student(name, lastName, cf, birthPlace, telephone,
                birthDate, city, cap, street, streetNumber);
    }

    public static void showAvailableCourses(List<Course> courses) {
        System.out.println("\n--- CORSI DISPONIBILI ---");
        System.out.println("ID\tLivello\t\tData inizio\tStato");
        int index = 1;
        for (Course course : courses) {
            System.out.printf("%d\t%d\t%-10s\t%s\t%s%n",
                    index++,
                    course.getCourseID(),
                    course.getLevel(),
                    DateUtils.formatDate(course.getActivationDate()),
                    course.isActive() ? "Attivo" : "Non attivo");
        }
    }

    public static Course chooseCourse(List<Course> courses) throws IOException {
        if (courses == null || courses.isEmpty()) {
            System.out.println("Nessun corso disponibile.");
            return null;
        }

        showAvailableCourses(courses);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int scelta = -1;

        do {
            System.out.print("Seleziona un corso (1-" + courses.size() + "): ");
            String input = reader.readLine();

            try {
                scelta = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Input non valido, inserisci un numero intero.");
                continue;
            }

            if (scelta < 1 || scelta > courses.size()) {
                System.out.println("Numero fuori intervallo. Riprova.");
            }

        } while (scelta < 1 || scelta > courses.size());

        return courses.get(scelta - 1);
    }

    public static int askStudentStatus() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int choice = -1;
        while(true){
            System.out.println("\n--- TIPO DI STUDENTE ---");
            System.out.println("1. Studente nuovo");
            System.out.println("2. Studente esistente");
            System.out.print("Scelta: ");
            choice = Integer.parseInt(reader.readLine().trim());
            if (choice == 1 || choice == 2) {
                break;
            }
            showInvalidOption();
        }
        return choice;
    }

    public static void showConflictingLessons(List<Lesson> conflictingLessons) {
        System.out.println("\n--- CONFLITTI DI ORARIO TROVATI ---");
        System.out.println("Le seguenti lezioni si sovrappongono con corsi attivi esistenti:");

        for (Lesson lesson : conflictingLessons) {
            System.out.printf("- %s: %s (%s - %s) in aula %s%n",
                    lesson.getDayOfWeek(),
                    lesson.getLevel(),
                    lesson.getStartTime(),
                    lesson.getEndTime(),
                    lesson.getClassroom());
        }

        System.out.println("\nSi prega di modificare gli orari o le aule e riprovare.");
    }

    public static Teacher inputTeacherDetails() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("\n--- INSERIMENTO NUOVO INSEGNANTE ---");

        System.out.print("Nome: ");
        String name = reader.readLine().trim();

        System.out.print("Cognome: ");
        String lastName = reader.readLine().trim();

        System.out.print("Nazione (es. IT): ");
        String nation = reader.readLine().trim();

        System.out.println("\n--- INDIRIZZO ---");
        System.out.print("Città: ");
        String city = reader.readLine().trim();

        System.out.print("CAP: ");
        String cap = reader.readLine().trim();

        System.out.print("Via: ");
        String street = reader.readLine().trim();

        System.out.print("Numero civico: ");
        int streetNumber = Integer.parseInt(reader.readLine().trim());

        // active = true di default
        return new Teacher(name, lastName, nation, true, city, cap, street, streetNumber);
    }

    //selezioni alcuni insegnanti dal totale
    public static List<Teacher> getSomeTeachers(List<Teacher> teachers) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.print("Inserisci i numeri dei docenti separati da virgola: ");
        String[] input = reader.readLine().trim().split(",");

        List<Teacher> selected = new ArrayList<>();
        for (String s : input) {
            try {
                int index = Integer.parseInt(s.trim()) - 1;
                if (index >= 0 && index < teachers.size()) {
                    selected.add(teachers.get(index));
                }
            } catch (NumberFormatException e) {
                System.out.println("Indice non valido: " + s);
            }
        }
        return selected;
    }

    public static String insertMontlyReportData(List<Teacher> teachers) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        // Mostra gli insegnanti inclusi nel report
        System.out.println("\n=== INSEGNANTI NEL REPORT ===");
        for (Teacher t : teachers) {
            System.out.printf("ID: %d - %s %s%n",
                    t.getTeacherID(), t.getName(), t.getLastName());
        }

        // Chiede il commento
        System.out.print("\nInserisci il commento per il report: ");
        String note = reader.readLine().trim();

        // Crea la stringa con gli ID separati da virgola
        String ids = teachers.stream()
                .map(t -> String.valueOf(t.getTeacherID()))
                .collect(Collectors.joining(","));

        // Restituisce il report formattato
        return ids + "\n" + "-".repeat(30) + "\n" + note;
    }



    public static void showInvalidOption() {
        System.out.println("Opzione non valida, riprova.");
    }

}
