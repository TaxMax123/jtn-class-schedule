package jtn.classSchedule.backend.api;

import jtn.classSchedule.backend.api.dto.Classroom;
import jtn.classSchedule.backend.api.dto.Professor;
import jtn.classSchedule.backend.api.dto.RecurringEvent;
import jtn.classSchedule.backend.persistence.course.Course;
import jtn.classSchedule.backend.service.course.CourseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Component
@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/allevents")
    public List<RecurringEvent> allEvents() {
        return courseService.getRecurringEvents();
    }

    @GetMapping("/allevents/{username}")
    public List<RecurringEvent> allUserEvents(@PathVariable("username") String username) {
        return courseService.getRecurringEvents(username);
    }

    @GetMapping("/course/{course}")
    public ArrayList<RecurringEvent> getEventsByCourseDto(@PathVariable("course") String course) {
        return courseService.getCoursesByCourseDto(course);
    }

    @GetMapping("/classroom")
    public ArrayList<Classroom> getClassrooms() {
        return courseService.getClassrooms();
    }

    @GetMapping("/classroom/{classroom}")
    public ArrayList<RecurringEvent> getEventsByClassroom(@PathVariable("classroom") String classroom) {
        return courseService.getCoursesByClassroom(classroom);
    }

    @GetMapping("/professors")
    public ArrayList<Professor> getProfessors() {
        return courseService.getProfessors();
    }

    @GetMapping("professor/{professor}")
    public ArrayList<RecurringEvent> getEventsByProfessor(@PathVariable(value = "professor") String professor) {
        return courseService.getCoursesByProfessor(professor);
    }

    @GetMapping("/find/{UUID}")
    public Course findById(@PathVariable String UUID) {
        return courseService.getCourseById(UUID);
    }
}
