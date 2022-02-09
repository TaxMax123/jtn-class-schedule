package jtn.classSchedule.backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class CoursePostDto {
    private final String id;
    private final String programShortName;
    private final String name;
    private final String shortName;
    private final String holder;
    private final String lecturer;
    private final String lectureType;
    private final String classroom;
    private final String startTime;
    private final String endTime;
    private final String weekday;
    private final Integer year;
    private final Integer semester;
    private final Integer repeatTimes;
    private final Integer startWeek;
    private final Integer classroomCapacity;

    private Boolean isValidField(String field) {
        return field != null && !field.isEmpty() && !field.isBlank();
    }

    private Boolean isValidField(Integer field) {
        return field != null && !field.toString().isBlank() && !field.toString().isEmpty();
    }

    public String isValid() {
        String invalidField = "";
        if (!isValidField(id)) {
            invalidField = "id";
        } else if (!isValidField(programShortName)) {
            invalidField = "programShortName";
        } else if (!isValidField(name)) {
            invalidField = "name";
        } else if (!isValidField(shortName)) {
            invalidField = "shortName";
        } else if (!isValidField(holder)) {
            invalidField = "holder";
        } else if (!isValidField(lectureType)) {
            invalidField = "lecturer";
        } else if (!isValidField(lectureType)) {
            invalidField = "lectureType";
        } else if (!isValidField(classroom)) {
            invalidField = "classroom";
        } else if (!isValidField(startTime)) {
            invalidField = "startTime";
        } else if (!isValidField(endTime)) {
            invalidField = "endTime";
        } else if (!isValidField(weekday)) {
            invalidField = "weekday";
        } else if (!isValidField(year)) {
            invalidField = "year";
        } else if (!isValidField(semester)) {
            invalidField = "semester";
        } else if (!isValidField(repeatTimes)) {
            invalidField = "repeatTimes";
        } else if (!isValidField(startWeek)) {
            invalidField = "startWeek";
        } else if (!isValidField(classroomCapacity)) {
            invalidField = "classroomCapacity";
        }
        return invalidField;
    }

}
