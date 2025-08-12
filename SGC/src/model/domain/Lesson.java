package model.domain;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Objects;

public class Lesson {
    private int progressiveCode;
    private final LevelName level;
    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;
    private final String classroom;

    public Lesson(int progressiveCode, LevelName level,
                  DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, String classroom) {
        this.progressiveCode = progressiveCode;
        this.level = level;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroom = classroom;
    }

    public void setProgressiveCode(int progressiveCode) {
        this.progressiveCode = progressiveCode;
    }

    // Getters
    public int getProgressiveCode() {
        return progressiveCode;
    }

    public LevelName getLevel() {
        return level;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    // Calcola la durata in minuti
    public int getDurationMinutes() {
        return (int) java.time.Duration.between(startTime, endTime).toMinutes();
    }

    public String getClassroom() {
        return classroom;
    }

    // Formattazione per visualizzazione
    public String getFormattedTime() {
        return String.format("%02d:%02d - %02d:%02d",
                startTime.getHour(), startTime.getMinute(),
                endTime.getHour(), endTime.getMinute());
    }

    @Override
    public String toString() {
        return dayOfWeek + " " + getFormattedTime() + " (Aula: " + classroom + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lesson lesson = (Lesson) o;
        return progressiveCode == lesson.progressiveCode &&
                level == lesson.level &&
                dayOfWeek == lesson.dayOfWeek &&
                Objects.equals(startTime, lesson.startTime) &&
                Objects.equals(endTime, lesson.endTime) &&
                Objects.equals(classroom, lesson.classroom); // Aggiunto controllo aula
    }
}

