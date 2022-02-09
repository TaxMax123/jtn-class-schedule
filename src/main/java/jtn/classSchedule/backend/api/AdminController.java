package jtn.classSchedule.backend.api;

import jtn.classSchedule.backend.api.dto.CoursePostDto;
import jtn.classSchedule.backend.api.dto.RegistrationDto;
import jtn.classSchedule.backend.api.dto.Semester;
import jtn.classSchedule.backend.response.CustomResponseEntity;
import jtn.classSchedule.backend.service.systemAdmin.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sys-ad")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/add-course")
    public ResponseEntity<Object> addCourse(@RequestHeader(value = "Authorization", required = false) String auth,
                                            @RequestBody CoursePostDto coursePostDto) {
        return adminService.newCourseFromPostDto(coursePostDto, auth);
    }

    @DeleteMapping("/delete-course/{id}")
    public CustomResponseEntity deleteCourse(@RequestHeader(value = "Authorization", required = false) String auth,
                                             @PathVariable String id) {
        return adminService.deleteCourse(id, auth);
    }

    @PostMapping("/add-user")
    public CustomResponseEntity registerUser(@RequestHeader(value = "Authorization", required = false) String auth,
                                             @RequestBody RegistrationDto registrationDto) {
        return adminService.registerUser(registrationDto, auth);
    }

    @PostMapping("/update-semester")
    public CustomResponseEntity updateSemester(@RequestHeader(value = "Authorization", required = false) String auth,
                                               @RequestBody Semester semester) {
        return adminService.updateSemesterStartAndEnd(semester, auth);
    }

    @PostMapping("/add-user-token")
    public CustomResponseEntity addUserToken(@RequestHeader(value = "Authorization", required = false) String auth,
                                             @RequestHeader(value = "Token", required = false) String token,
                                             @RequestHeader(value = "Code", required = false) String confirmation) {

        return adminService.addNewUserToken(auth, token, confirmation);
    }

    @PostMapping("/remove-user-token")
    public CustomResponseEntity removeUserToken(@RequestHeader(value = "Authorization", required = false) String auth,
                                                @RequestHeader(value = "Token", required = false) String token,
                                                @RequestHeader(value = "Code", required = false) String confirmation) {

        return adminService.removeUserToken(auth, token, confirmation);
    }

    @PostMapping("/remove-all-user-tokens")
    public CustomResponseEntity removeAllUserToken(@RequestHeader(value = "Authorization", required = false) String auth,
                                                   @RequestHeader(value = "Code", required = false) String confirmation) {

        return adminService.removeAllUserTokens(auth, confirmation);
    }
}
