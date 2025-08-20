package model.Utils;

import model.domain.Absence;
import model.domain.Lesson;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

public class AbsenceUtils {
    public static boolean isAbsenceOnLessonDay(Absence absence, List<Lesson> lessons) {
        // Converte la data dell'assenza in un DayOfWeek
        LocalDate absenceDate = LocalDate.of(
                absence.getDate().getYear(),
                absence.getDate().getMonth(),
                absence.getDate().getDayOfMonth()
        );
        DayOfWeek absenceDayOfWeek = absenceDate.getDayOfWeek();

        // Controlla se almeno una lezione ha lo stesso dayOfWeek
        return lessons.stream()
                .anyMatch(lesson -> lesson.getDayOfWeek() == absenceDayOfWeek);
    }
}
