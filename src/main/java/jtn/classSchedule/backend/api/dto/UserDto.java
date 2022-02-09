package jtn.classSchedule.backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;

@Data
@Getter
@Builder
@AllArgsConstructor
public class UserDto {
    private String firstName;
    private String lastName;
    private String userName;
    private Integer role;
    private ArrayList<String> enrolledCourses;
    private Boolean allCoursesShown;
}
