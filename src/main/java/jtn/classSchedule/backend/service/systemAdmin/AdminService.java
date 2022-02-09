package jtn.classSchedule.backend.service.systemAdmin;

import jtn.classSchedule.backend.api.dto.CoursePostDto;
import jtn.classSchedule.backend.api.dto.RegistrationDto;
import jtn.classSchedule.backend.api.dto.Semester;
import jtn.classSchedule.backend.response.CustomResponseEntity;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;

public interface AdminService {
    ResponseEntity<Object> newCourseFromPostDto(CoursePostDto coursePostDto, String auth);

    CustomResponseEntity deleteCourse(String docId, String auth);

    CustomResponseEntity registerUser(RegistrationDto registrationDto, String auth);

    CustomResponseEntity updateSemesterStartAndEnd(Semester semester, String auth);

    CustomResponseEntity addNewUserToken(String auth, String token, String confirmation);

    CustomResponseEntity removeUserToken(String auth, String token, String confirmation);

    CustomResponseEntity removeAllUserTokens(String auth, String confirmation);

    ArrayList<String> getUserTokens();

    String getToken();
}
