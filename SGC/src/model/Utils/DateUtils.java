package model.Utils;

import java.time.DateTimeException;
import java.time.LocalDate;

public class DateUtils {

    public static LocalDate toLocalDate(int day, int month, int year) {
        try {
            return LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("Data non valida: " + day + "/" + month + "/" + year, e);
        }
    }

    public static int[] fromLocalDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return new int[]{
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear()
        };
    }

    public static String formatDate(LocalDate date) {
        if (date == null) return "N/D";
        return String.format("%02d/%02d/%d",
                date.getDayOfMonth(),
                date.getMonthValue(),
                date.getYear());
    }
}
