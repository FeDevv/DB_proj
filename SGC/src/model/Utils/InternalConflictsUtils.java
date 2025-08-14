package model.Utils;

import model.domain.Lesson;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InternalConflictsUtils {
    public static boolean hasInternalConflicts(List<Lesson> lessons) {
        // Ordina per giorno, classe e orario
        List<Lesson> sortedLessons = new ArrayList<>(lessons);
        sortedLessons.sort(Comparator.comparing(Lesson::getDayOfWeek)
                .thenComparing(Lesson::getClassroom) // oppure getClassName se è stringa
                .thenComparing(Lesson::getStartTime));

        // Controlla sovrapposizioni
        for (int i = 1; i < sortedLessons.size(); i++) {
            Lesson prev = sortedLessons.get(i - 1);
            Lesson current = sortedLessons.get(i);

            // Stesso giorno e stessa classe
            if (prev.getDayOfWeek().equals(current.getDayOfWeek()) &&
                    prev.getClassroom().equals(current.getClassroom())) {

                LocalTime prevEnd = prev.getEndTime();
                LocalTime currentStart = current.getStartTime();

                // Se l'orario di fine della precedente è dopo l'orario di inizio della successiva → conflitto
                if (prevEnd.isAfter(currentStart)) {
                    return true;
                }
            }
        }
        return false;
    }
}
