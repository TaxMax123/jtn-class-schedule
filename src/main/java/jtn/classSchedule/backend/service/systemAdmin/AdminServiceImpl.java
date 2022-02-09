package jtn.classSchedule.backend.service.systemAdmin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jtn.classSchedule.backend.api.dto.CoursePostDto;
import jtn.classSchedule.backend.api.dto.RegistrationDto;
import jtn.classSchedule.backend.api.dto.Semester;
import jtn.classSchedule.backend.config.Token;
import jtn.classSchedule.backend.config.UserTokens;
import jtn.classSchedule.backend.persistence.course.Course;
import jtn.classSchedule.backend.persistence.course.CourseRepository;
import jtn.classSchedule.backend.persistence.user.UserRepository;
import jtn.classSchedule.backend.response.CustomResponseEntity;
import jtn.classSchedule.backend.service.course.CourseMapper;
import jtn.classSchedule.backend.service.user.UserMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.Range;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final Token token;
    private final UserTokens userTokens;
    private final CourseRepository courseRepository;
    private final CourseMapper MAPPER = Mappers.getMapper(CourseMapper.class);

    private final UserRepository userRepository;
    private final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);

    @Override
    public ResponseEntity<Object> newCourseFromPostDto(CoursePostDto coursePostDto, String auth) {
        if (!isAuth(auth)) {
            return new ResponseEntity<>(CustomResponseEntity.builder().responseCode(HttpStatus.UNAUTHORIZED.value()).responsePhrase(HttpStatus.UNAUTHORIZED.getReasonPhrase()).description("Access denied, not authorized").build(), HttpStatus.UNAUTHORIZED);
        }
        String invalidField = coursePostDto.isValid();
        if (invalidField != null && !invalidField.isBlank() && !invalidField.isEmpty()) {
            return new ResponseEntity<>(CustomResponseEntity.builder().responseCode(HttpStatus.NOT_ACCEPTABLE.value()).responsePhrase(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase()).description("Field '" + invalidField + "' has invalid value").build(), HttpStatus.NOT_ACCEPTABLE);
        }
        String classroomConflictingDoc = checkClassroomTimeOverlap(coursePostDto);
        if (classroomConflictingDoc != null && !classroomConflictingDoc.isEmpty() && !classroomConflictingDoc.isBlank()) {
            return new ResponseEntity<>(CustomResponseEntity.builder().responseCode(HttpStatus.CONFLICT.value()).responsePhrase(HttpStatus.CONFLICT.getReasonPhrase()).description("Conflict in time with existing document " + classroomConflictingDoc).build(), HttpStatus.CONFLICT);
        }
        String conflictingDocument = checkExists(coursePostDto);
        if (conflictingDocument != null && !conflictingDocument.isBlank() && !conflictingDocument.isEmpty())
            return new ResponseEntity<>(CustomResponseEntity.builder().responseCode(HttpStatus.CONFLICT.value()).responsePhrase(HttpStatus.CONFLICT.getReasonPhrase()).description("Conflict in time with existing document " + conflictingDocument).build(), HttpStatus.CONFLICT);

        try {
            var newCourse = MAPPER.postDtoToCourse(coursePostDto);
            newCourse.setActive(true);
            newCourse.setDocId(UUID.randomUUID().toString());
            newCourse = courseRepository.save(newCourse);
            var resp = CustomResponseEntity.builder().responseCode(HttpStatus.CREATED.value()).responsePhrase(HttpStatus.CREATED.getReasonPhrase()).description("Id of created document: " + newCourse.getDocId()).build();
            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(CustomResponseEntity.builder().responseCode(HttpStatus.INTERNAL_SERVER_ERROR.value()).responsePhrase(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase()).description("Error while saving data, reason UNKNOWN").build(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    private Boolean checkWeek(Course course, CoursePostDto coursePostDto) {
        int startWeekR = course.getStartWeek();
        int endWeekR = course.getRepeatTimes() + startWeekR;
        int startWeekN = coursePostDto.getStartWeek();
        int endWeekN = coursePostDto.getRepeatTimes() + startWeekR;
        return ((startWeekN >= startWeekR && startWeekN <= endWeekR) || (endWeekN >= startWeekR && endWeekN <= endWeekR));
    }

    private Boolean checkTime(Course course, CoursePostDto coursePostDto) {
        var formatter = DateTimeFormatter.ofPattern("H:mm");
        var startTimeR = LocalTime.parse(course.getStartTime(), formatter);
        var endTimeR = LocalTime.parse(course.getEndTime(), formatter);
        var startTimeN = LocalTime.parse(coursePostDto.getStartTime(), formatter);
        var endTimeN = LocalTime.parse(coursePostDto.getEndTime(), formatter);
        return (startTimeN.isAfter(startTimeR) && startTimeN.isBefore(endTimeR)) || (endTimeN.isAfter(startTimeR) && endTimeN.isBefore(endTimeR));
    }

    private Boolean checkTimeAsString(Course course, CoursePostDto coursePostDto) {
        var startTimeStrR = course.getStartTime();
        var endTimeStrR = course.getEndTime();
        var startTimeStrN = coursePostDto.getStartTime();
        var endTimeStrN = coursePostDto.getEndTime();
        return (startTimeStrR.equals(startTimeStrN) || endTimeStrR.equals(endTimeStrN));
    }

    private Boolean hasTimeOverlap(Course course, CoursePostDto coursePostDto) {
        return (checkWeek(course, coursePostDto) && (checkTime(course, coursePostDto) || checkTimeAsString(course, coursePostDto)));
    }

    private String checkClassroomTimeOverlap(CoursePostDto coursePostDto) {
        var searchResult = courseRepository.findCoursesByClassroomAndWeekday(coursePostDto.getClassroom(), coursePostDto.getWeekday());
        for (var course : searchResult) {
            if (hasTimeOverlap(course, coursePostDto)) return course.getDocId();
        }
        return "";
    }

    private String checkExists(CoursePostDto coursePostDto) {
        var searchResult = courseRepository.findCoursesByIdAndProgramShortNameAndNameAndShortNameAndHolderAndLecturerAndLectureTypeAndClassroomAndWeekdayAndSemester(coursePostDto.getId(), coursePostDto.getProgramShortName(), coursePostDto.getName(), coursePostDto.getShortName(), coursePostDto.getHolder(), coursePostDto.getLecturer(), coursePostDto.getLectureType(), coursePostDto.getClassroom(), coursePostDto.getWeekday(), coursePostDto.getSemester());
        if (!searchResult.isEmpty()) {
            for (var r : searchResult) {
                String conflictingDoc = r.getDocId();
                if (checkWeek(r, coursePostDto) && (checkTime(r, coursePostDto) || checkTimeAsString(r, coursePostDto)))
                    return conflictingDoc;
            }
        }
        return "";
    }

    private Boolean isAuth(String auth) {
        return auth != null && !auth.isEmpty() && !auth.isBlank() && auth.equals("Bearer " + token.toString());
    }

    @Override
    public CustomResponseEntity deleteCourse(String docId, String auth) {
        if (!isAuth(auth)) {
            return new CustomResponseEntity(403, "Unauthorized", "Access denied. ");
        }
        var entity = courseRepository.findCourseByDocId(docId);
        if (entity == null) {
            return new CustomResponseEntity(404, "Bad request", "Non existing id received. ");
        }
        courseRepository.delete(entity);
        return new CustomResponseEntity(204, "Resource deleted successfully", "Resource deleted successfully. ");
    }

    private Boolean mandatoryFieldsAreNull(RegistrationDto registrationDto) {
        return Stream.of(registrationDto.getFirstName(), registrationDto.getLastName(), registrationDto.getUserName(), registrationDto.getPwd(), registrationDto.getRepeatPwd(), registrationDto.getRole()).anyMatch(Objects::isNull);
    }

    private Boolean isUsernameFormat(String username) {
        var pattern = Pattern.compile("[.][a,z]{2,}");
        var matcher = pattern.matcher(username);
        System.out.println("\n\n" + matcher + "\t" + matcher);
        return (username.contains("@") && (!username.startsWith("@") || !username.startsWith(".")) && (username.split("@")[0].length() >= 7) && username.endsWith(String.valueOf(matcher)));
    }

    private Boolean isAvailableUserName(String username) {
        return userRepository.findUserByUserName(username) == null;
    }

    private Boolean isValidPassword(RegistrationDto registrationDto) {
        var first = registrationDto.getPwd();
        var second = registrationDto.getRepeatPwd();
        registrationDto.setPwd(BCrypt.hashpw(registrationDto.getPwd(), BCrypt.gensalt("$2a")));
        return first.equals(second);
    }

    private ArrayList<Course> getCourses(RegistrationDto registrationDto) {
        var list = new ArrayList<Course>();
        registrationDto.getEnrolledCourses().forEach(course -> list.add(courseRepository.findCourseByDocId(course)));
        return list;
    }

    private Boolean isValidYear(Integer year, Course course) {
        return year.equals(course.getYear()) || year.equals(course.getYear() - 1);
    }

    private Boolean isValidCourseEnrollment(RegistrationDto registrationDto) {
        if (registrationDto.getYear() == null || registrationDto.getEnrolledCourses() == null) {
            return false;
        }
        var courses = getCourses(registrationDto);
        if (courses.isEmpty()) {
            return false;
        }
        return courses.stream().allMatch(programName -> programName.getProgramShortName().equals(courses.get(0).getProgramShortName())) && courses.stream().allMatch(course -> isValidYear(registrationDto.getYear(), course));
    }

    private void setHigherRoleData(RegistrationDto registrationDto) {
        registrationDto.setYear(0);
        registrationDto.setEnrolledCourses(null);
    }

    private Boolean roleValidation(RegistrationDto registrationDto) {
        var properRole = Range.between(1, 3);
        if (!properRole.contains(registrationDto.getRole())) {
            return false;
        }
        switch (registrationDto.getRole()) {
            case 1, 2: {
                setHigherRoleData(registrationDto);
                return true;
            }
            case 3:
                return isValidCourseEnrollment(registrationDto);
            default:
                return false;
        }
    }

    @Override
    public CustomResponseEntity registerUser(RegistrationDto registrationDto, String auth) {
        if (!isAuth(auth)) {
            return new CustomResponseEntity(403, "Unauthorized", "Access denied. ");
        }
        if (mandatoryFieldsAreNull(registrationDto)) {
            return new CustomResponseEntity(404, "Bad request", "Not all mandatory fields are present. ");
        }
        if (!isAvailableUserName(registrationDto.getUserName())) {
            return new CustomResponseEntity(404, "Bad request", "User with " + registrationDto.getUserName() + " name already exists. ");
        }
        if (!isValidPassword(registrationDto)) {
            return new CustomResponseEntity(404, "Bad request", "Invalid repeat password. ");
        }
        if (!roleValidation(registrationDto)) {
            return new CustomResponseEntity(404, "Bad request", "Incorrect role or courses value. ");
        }
        var entity = USER_MAPPER.dtoToEntity(registrationDto);
        entity.setAllCoursesShown(true);
        userRepository.save(entity);
        return new CustomResponseEntity(201, "Created", "User successfully created. ");
    }

    private void writeFile(Semester semester) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(new File("src/resources/semester.json"), semester);
    }

    private Semester readFile() {
        var mapper = new ObjectMapper();
        try {
            String path = "src/resources/semester.json";
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            String json = new String(encoded, StandardCharsets.UTF_8);
            return mapper.reader().forType(new TypeReference<Semester>() {
            }).readValue(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Semester setValues(Semester semester, Semester newValues) {
        if (newValues.getFirstSemesterStart() == null || newValues.getFirstSemesterStart().isEmpty()) {
            newValues.setFirstSemesterStart(semester.getFirstSemesterStart());
        }
        if (newValues.getFirstSemesterEnd() == null || newValues.getFirstSemesterEnd().isEmpty()) {
            newValues.setFirstSemesterEnd(semester.getFirstSemesterEnd());
        }
        if (newValues.getSecondSemesterStart() == null || newValues.getSecondSemesterStart().isEmpty()) {
            newValues.setSecondSemesterStart(semester.getSecondSemesterStart());
        }
        if (newValues.getSecondSemesterEnd() == null || newValues.getSecondSemesterEnd().isEmpty()) {
            newValues.setSecondSemesterEnd(semester.getSecondSemesterEnd());
        }
        return newValues;
    }

    private CustomResponseEntity updateSemester(Semester semester, Semester newValues) {
        var newSemesterValues = setValues(semester, newValues);
        var currentYear = LocalDateTime.now().getYear();
        int a, b, c, d;
        a = b = c = d = 0;
        var summerVacation = LocalDateTime.parse(currentYear + newSemesterValues.getSecondSemesterEnd()).getMonth().plus(1);
        if (LocalDateTime.now().compareTo(LocalDateTime.parse(currentYear + newSemesterValues.getSecondSemesterStart())) < 0) {
            a = -1;
        } else if (LocalDateTime.now().compareTo(LocalDateTime.parse(currentYear + newSemesterValues.getSecondSemesterStart())) > 0) {
            if (LocalDateTime.now().getMonthValue() < Integer.parseInt(summerVacation.toString())) {
                a = -1;
            } else {
                b = c = d = 1;
            }
        }
        var startOfFirstSemester = LocalDateTime.parse(currentYear + a + newSemesterValues.getFirstSemesterStart());
        var endOfFirstSemester = LocalDateTime.parse(currentYear + b + newSemesterValues.getFirstSemesterEnd());
        var startOfSecondSemester = LocalDateTime.parse(currentYear + c + newSemesterValues.getSecondSemesterStart());
        var endOfSecondSemester = LocalDateTime.parse(currentYear + d + newSemesterValues.getSecondSemesterEnd());

        if (startOfFirstSemester.isBefore(endOfFirstSemester) && endOfFirstSemester.isBefore(startOfSecondSemester) && startOfSecondSemester.isBefore(endOfSecondSemester)) {
            try {
                writeFile(newSemesterValues);
                return new CustomResponseEntity(201, "Created", "Semester data successfully updated. ");
            } catch (IOException e) {
                e.printStackTrace();
                return new CustomResponseEntity(500, "Internal server error", "Error writing in file. ");
            }
        }
        return new CustomResponseEntity(404, "Bad request", "Dates not set properly. ");
    }

    private Boolean validSemesterProperties(Semester semester) {
        try {
            LocalDateTime.parse(LocalDateTime.now().getYear() + semester.getFirstSemesterStart());
            LocalDateTime.parse(LocalDateTime.now().getYear() + semester.getFirstSemesterEnd());
            LocalDateTime.parse(LocalDateTime.now().getYear() + semester.getSecondSemesterStart());
            LocalDateTime.parse(LocalDateTime.now().getYear() + semester.getSecondSemesterEnd());
        } catch (Exception e) {
            //e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public CustomResponseEntity updateSemesterStartAndEnd(Semester semester, String auth) {
        if (!isAuth(auth)) {
            return new CustomResponseEntity(403, "Unauthorized", "Access denied. ");
        }
        if (semester == null) {
            return new CustomResponseEntity(404, "Bad request", "Body object empty or non existing. ");
        }
        var oldValues = readFile();
        if (oldValues == null) {
            return new CustomResponseEntity(600, "I am a teapot", "Error reading file. ");
        }
        if (!validSemesterProperties(semester)) {
            return new CustomResponseEntity(404, "Bad request", "Invalid DateTimeFormat. ");
        }
        return updateSemester(oldValues, semester);
    }

    private Boolean isAuthAndConfirmed(String auth, String token, String confirmation) {
        var auth_bearer = auth.split(" ")[1];
        var check_cd_code = new String(Base64.getEncoder().encode(String.format("%s:%s", auth_bearer, token).getBytes()));
        return isAuth(auth) && check_cd_code.equals(confirmation);
    }

    @Override
    public CustomResponseEntity addNewUserToken(String auth, String token, String confirmation) {
        if (token == null || token.isEmpty() || token.isBlank() || confirmation == null || confirmation.isBlank() || confirmation.isEmpty()) {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.NOT_ACCEPTABLE.value())
                    .responsePhrase(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                    .responsePhrase("Cannot accept token or confirmation code, it is null or empty")
                    .build();
        }
        if (!isAuthAndConfirmed(auth, token, confirmation)) {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.UNAUTHORIZED.value())
                    .responsePhrase(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .description("Could not verify token and/or confirmation code")
                    .build();
        }
        if (userTokens.addUserToken(token)) {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.CREATED.value())
                    .responsePhrase(HttpStatus.CREATED.getReasonPhrase())
                    .description("User token added successfully")
                    .build();
        } else {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.CONFLICT.value())
                    .responsePhrase(HttpStatus.CONFLICT.getReasonPhrase())
                    .description("User token exists")
                    .build();
        }
    }

    @Override
    public CustomResponseEntity removeUserToken(String auth, String token, String confirmation) {
        if (token == null || token.isEmpty() || token.isBlank() || confirmation == null || confirmation.isBlank() || confirmation.isEmpty()) {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.NOT_ACCEPTABLE.value())
                    .responsePhrase(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                    .responsePhrase("Cannot accept token or confirmation code, it is null or empty")
                    .build();
        }
        if (!isAuthAndConfirmed(auth, token, confirmation)) {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.UNAUTHORIZED.value())
                    .responsePhrase(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .description("Could not verify token and/or confirmation code")
                    .build();
        }
        if (userTokens.removeUserToken(token)) {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.OK.value())
                    .responsePhrase(HttpStatus.OK.getReasonPhrase())
                    .description("User token deleted successfully")
                    .build();
        } else {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.NOT_ACCEPTABLE.value())
                    .responsePhrase(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                    .description("Could not delete user token, it does not exists")
                    .build();
        }
    }

    @Override
    public CustomResponseEntity removeAllUserTokens(String auth, String confirmation) {
        if (confirmation == null || confirmation.isBlank() || confirmation.isEmpty()) {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.NOT_ACCEPTABLE.value())
                    .responsePhrase(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                    .responsePhrase("Cannot accept confirmation code it is null or empty")
                    .build();
        }
        if (!isAuthAndConfirmed(auth, "deleteAll", confirmation)) {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.UNAUTHORIZED.value())
                    .responsePhrase(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                    .description("Could not verify token and/or confirmation code")
                    .build();
        }
        if (!userTokens.removeAllUserTokens()) {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.OK.value())
                    .responsePhrase(HttpStatus.OK.getReasonPhrase())
                    .description("User token deleted successfully")
                    .build();
        } else {
            return CustomResponseEntity.builder()
                    .responseCode(HttpStatus.NOT_ACCEPTABLE.value())
                    .responsePhrase(HttpStatus.NOT_ACCEPTABLE.getReasonPhrase())
                    .description("Could not delete user tokens")
                    .build();
        }
    }

    @Override
    public ArrayList<String> getUserTokens() {
        return userTokens.getTokens();
    }

    @Override
    public String getToken() {
        return token.toString();
    }

}
