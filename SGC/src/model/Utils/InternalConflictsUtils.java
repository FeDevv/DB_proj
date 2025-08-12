package model.Utils;

import model.domain.Lesson;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class InternalConflictsUtils {
    public static boolean hasInternalConflicts(List<Lesson> lessons) {
        // Ordina le lezioni per giorno e orario
        List<Lesson> sortedLessons = new ArrayList<>(lessons);
        sortedLessons.sort(Comparator.comparing(Lesson::getDayOfWeek)
                .thenComparing(Lesson::getStartTime));

        // Controlla sovrapposizioni
        for (int i = 1; i < sortedLessons.size(); i++) {
            Lesson prev = sortedLessons.get(i - 1);
            Lesson current = sortedLessons.get(i);

            if (prev.getDayOfWeek().equals(current.getDayOfWeek())) {
                LocalTime prevEnd = prev.getEndTime();
                LocalTime currentStart = current.getStartTime();

                if (prevEnd.isAfter(currentStart)) {
                    return true;
                }
            }
        }
        return false;
    }
}
