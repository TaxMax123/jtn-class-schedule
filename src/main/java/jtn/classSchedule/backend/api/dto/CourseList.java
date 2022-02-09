package jtn.classSchedule.backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@AllArgsConstructor
@Getter
@Setter
public class CourseList {
    private String courseId;
    private String name;
    private String lectureType;
    private String lecturer;
    private String start;
    private String end;
    private String day;
    private Integer numericDay;
    private Integer semester;
    private String classroom;

    public String timeToString() {

        return reformatHour(this.start) + '-' + reformatHour(this.end);
    }

    private String reformatHour(String hour) {
        return String.format("%02d:%02d", Integer.valueOf(hour.split(":")[0]), Integer.valueOf(hour.split(":")[1]));
    }

    public static class WeekdayComparator implements Comparator<CourseList> {
        @Override
        public int compare(CourseList o1, CourseList o2) {
            return Integer.compare(o1.getNumericDay(), o2.getNumericDay());
        }
    }

    public static class TimeComparator implements Comparator<CourseList> {
        @Override
        public int compare(CourseList o1, CourseList o2) {
            return o1.timeToString().compareTo(o2.timeToString());
        }
    }
}