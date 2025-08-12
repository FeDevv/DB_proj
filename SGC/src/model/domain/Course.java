package model.domain;

import java.time.LocalDate;

public class Course {
    private Integer courseID;
    private final LevelName level;
    private final LocalDate activationDate;
    private final boolean active;

    public Course(LevelName level, LocalDate activationDate, boolean active) {
        this.courseID = null;
        this.level = level;
        this.activationDate = activationDate;
        this.active = active;
    }

    // Getters
    public int getCourseID() { return courseID; }
    public LevelName getLevel() { return level; }
    public LocalDate getActivationDate() { return activationDate; }
    public boolean isActive() { return active; }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    @Override
    public String toString() {
        String status = active ? "Attivo" : "Non attivo";
        return "Corso " + courseID + " - " + level + " (" + activationDate + ") " + status;
    }
}

