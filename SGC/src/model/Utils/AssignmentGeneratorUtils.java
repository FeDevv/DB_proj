package model.Utils;

import model.domain.Assignment;
import model.domain.Lesson;
import model.domain.Teacher;

import java.util.ArrayList;
import java.util.List;

public class AssignmentGeneratorUtils {
    public static List<Assignment> generateAssignments(List<Teacher> teachers,
                                                       List<Lesson> lessons,
                                                       int courseCode) {
        List<Assignment> assignments = new ArrayList<>();

        for (Teacher teacher : teachers) {
            for (Lesson lesson : lessons) {
                assignments.add(new Assignment(
                        teacher.getTeacherID(),
                        courseCode,
                        lesson.getLevel(),
                        lesson.getStartTime(),
                        lesson.getEndTime()
                ));
            }
        }

        return assignments;
    }
}
