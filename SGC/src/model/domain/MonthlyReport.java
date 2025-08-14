package model.domain;

import java.time.LocalDate;

import java.time.LocalDate;
import java.util.Objects;

public class MonthlyReport {
    private Integer reportID;
    private LocalDate date;
    private String data;

    // Costruttore vuoto
    public MonthlyReport() {
    }

    // Costruttore senza ID (utile per nuove creazioni)
    public MonthlyReport(LocalDate date, String data) {
        this.date = date;
        this.data = data;
    }

    // Costruttore completo
    public MonthlyReport(Integer reportID, LocalDate date, String data) {
        this.reportID = reportID;
        this.date = date;
        this.data = data;
    }

    // Getter e Setter
    public Integer getReportID() {
        return reportID;
    }

    public void setReportID(Integer reportID) {
        this.reportID = reportID;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getYear() {
        return date.getYear();
    }

    public int getMonthValue() {
        return date.getMonthValue();
    }

    // toString
    @Override
    public String toString() {
        return "MonthlyReport{" +
                "reportID=" + reportID +
                ", date=" + date +
                ", data='" + data + '\'' +
                '}';
    }
}
