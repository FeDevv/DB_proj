package model.domain;

import model.Utils.DateUtils;

import java.time.LocalDate;

public class Absence {
    private int studentId;
    private LocalDate date;


    public Absence(int studentId, LocalDate date) {
        this.studentId = studentId;
        this.date = date;
    }

    // Getters
    public int getStudentId() {
        return studentId;
    }

    public LocalDate getDate() {
        return date;
    }

}
