package jtn.classSchedule.backend.service.course;

import jtn.classSchedule.backend.api.dto.*;
import jtn.classSchedule.backend.persistence.course.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@Component
public interface CourseService {

    ArrayList<Course> getCourses();

    ArrayList<RecurringEvent> getRecurringEvents(String... userName);

    ArrayList<CourseDto> getCourseDtos();

    ArrayList<RecurringEvent> getCoursesByCourseDto(String courseDtoName);

    ArrayList<Classroom> getClassrooms();

    ArrayList<RecurringEvent> getCoursesByClassroom(String classroom);

    ArrayList<Professor> getProfessors();

    ArrayList<RecurringEvent> getCoursesByProfessor(String professor);

    Page<CourseList> getListOfCoursesBySemester(String userName, String semester, Pageable pageable);

    Course getCourseById(String id);

    void processRecEvents(ArrayList<RecurringEvent> recurringEventArrayList);
}
