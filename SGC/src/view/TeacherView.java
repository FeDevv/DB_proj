package view;

import model.domain.Course;
import model.Utils.DateUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class TeacherView {

    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void showWelcomeMessage(String username) {
        System.out.println("Accesso come INSEGNANTE");
        System.out.println("Benevenuto/a, " + username);
    }

    public static int showMenu() {
        System.out.println("\n--- MENU INSEGNANTE ---");
        System.out.println("1. ST1 - Stampa elenco studenti del corso");
        System.out.println("2. VC1 - Visualizza corsi assegnati");
        System.out.println("3. ASa1 - Registrazione assenza studente");
        System.out.println("4. RP2 - Generazione report settimanale");
        System.out.println("5. VS1 - Visualizzazione storico assenze");
        System.out.println("0. Logout");
        System.out.print("Scelta >> ");
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            return Integer.parseInt(br.readLine());
        } catch (IOException | NumberFormatException e) {
            return -1;
        }
    }


    public static void showAssignedCourses(List<Course> courses, String username) {
        if (courses == null || courses.isEmpty()) {
            System.out.println("\n" + username + ", al momento non hai corsi assegnati.");
            return;
        }

        System.out.println("\n" + username + ", ecco i tuoi corsi assegnati:");
        System.out.println("----------------------------------------------------");
        System.out.printf("%-8s %-12s %-15s %-10s%n", "ID Corso", "Livello", "Data Inizio", "Stato");
        System.out.println("----------------------------------------------------");

        for (Course c : courses) {
            String startDate = DateUtils.formatDate(c.getActivationDate());
            String status = c.isActive() ? "Attivo" : "Non Attivo";

            System.out.printf("%-8d %-12s %-15s %-10s %s%n",
                    c.getCourseID(),
                    c.getLevel().toString(),
                    startDate,
                    status
                    );
        }
        System.out.println("----------------------------------------------------");
        System.out.println("Totale corsi assegnati: " + courses.size());
    }

    public static void showNoCoursesMessage(String username) {
        System.out.println("\n" + username + ", al momento non hai corsi assegnati.");
        System.out.println("Contatta l'amministrazione per ulteriori informazioni.");
    }

    public static void showInvalidOption() {
        System.out.println("Opzione non valida, riprova.");
    }
}
