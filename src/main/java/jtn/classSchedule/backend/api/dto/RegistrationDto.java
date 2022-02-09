package jtn.classSchedule.backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@AllArgsConstructor
public class RegistrationDto {
    private String firstName;
    private String lastName;
    private String userName;
    private String pwd;
    private String repeatPwd;
    private Integer role;
    private Integer year;
    private ArrayList<String> enrolledCourses;
}
