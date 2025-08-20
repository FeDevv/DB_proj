package model.domain;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;

public class WeeklyReport {
    private Integer weeklyReportID;
    private int weekNumber;       // Numero della settimana (1-52/53)
    private int year;             // Anno della settimana
    private LocalDate reportDate; // Data di creazione del report
    private String data;          // Dati del report

    // Costruttore vuoto
    public WeeklyReport() {
    }

    // Costruttore per nuova creazione
    public WeeklyReport(int weekNumber, int year, String data) {
        this.weekNumber = weekNumber;
        this.year = year;
        this.reportDate = LocalDate.now();
        this.data = data;
    }

    // Costruttore completo
    public WeeklyReport(Integer weeklyReportID, int weekNumber, int year, String data) {
        this.weeklyReportID = weeklyReportID;
        this.weekNumber = weekNumber;
        this.year = year;
        this.data = data;
    }

    // Getter e Setter
    public Integer getWeeklyReportID() {
        return weeklyReportID;
    }

    public void setWeeklyReportID(Integer weeklyReportID) {
        this.weeklyReportID = weeklyReportID;
    }

    public int getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(int weekNumber) {
        if (weekNumber < 1 || weekNumber > 53) {
            throw new IllegalArgumentException("Il numero della settimana deve essere tra 1 e 53");
        }
        this.weekNumber = weekNumber;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        if (year < 1900 || year > 2100) {
            throw new IllegalArgumentException("Anno non valido");
        }
        this.year = year;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDate reportDate) {
        this.reportDate = reportDate;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    // Metodo per ottenere la data di inizio settimana (lunedì)
    public LocalDate getStartOfWeek() {
        return LocalDate.of(year, 1, 1)
                .with(WeekFields.of(Locale.getDefault()).weekOfYear(), weekNumber)
                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1); // 1 = Lunedì
    }

    // Metodo per ottenere la data di fine settimana (domenica)
    public LocalDate getEndOfWeek() {
        return getStartOfWeek().plusDays(6);
    }

    // equals e hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeeklyReport that = (WeeklyReport) o;
        return weekNumber == that.weekNumber &&
                year == that.year &&
                Objects.equals(weeklyReportID, that.weeklyReportID) &&
                Objects.equals(reportDate, that.reportDate) &&
                Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(weeklyReportID, weekNumber, year, reportDate, data);
    }

    // toString
    @Override
    public String toString() {
        return "WeeklyReport{" +
                "weeklyReportID=" + weeklyReportID +
                ", weekNumber=" + weekNumber +
                ", year=" + year +
                ", reportDate=" + reportDate +
                ", data='" + data + '\'' +
                '}';
    }

    // Metodo factory per creare un report dalla data corrente
    public static WeeklyReport createForCurrentWeek(String data) {
        LocalDate now = LocalDate.now();
        int weekNumber = now.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        int year = now.getYear();
        return new WeeklyReport(weekNumber, year, data);
    }
}
