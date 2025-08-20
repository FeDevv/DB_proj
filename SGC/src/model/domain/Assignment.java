package model.domain;

import java.time.LocalTime;
import java.util.Objects;

public class Assignment {
    private final Integer teacherId;
    private final Integer courseCode;
    private final LevelName levelName;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public Assignment(int teacherId, int courseCode, LevelName levelName,
                      LocalTime startTime, LocalTime endTime) {
        this.teacherId = teacherId;
        this.courseCode = courseCode;
        this.levelName = levelName;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public int getTeacherId() {
        return teacherId;
    }

    public int getCourseCode() {
        return courseCode;
    }

    public LevelName getLevelName() {
        return levelName;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Assignment{" +
                "teacherId=" + teacherId +
                ", courseCode='" + courseCode + '\'' +
                ", levelName=" + levelName +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

}

