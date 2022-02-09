package jtn.classSchedule.backend.api;

import jtn.classSchedule.backend.api.dto.CourseList;
import jtn.classSchedule.backend.api.dto.HideAndSeek;
import jtn.classSchedule.backend.service.course.CourseService;
import jtn.classSchedule.backend.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@AllArgsConstructor
public class TimetableController {

    private final CourseService courseService;
    @Qualifier("jtnUserService")
    private final UserService userService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView indexCalendar(@AuthenticationPrincipal UserDetails currentUser) {
        var mav = new ModelAndView("calendar");
        if (currentUser != null) {
            mav.addObject("loggedInUser", userService.getLoggedInUser(currentUser.getUsername()));
            mav.addObject("hideAndSeek", userService.getCoursesVisibility(currentUser.getUsername()));
        }
        mav.addObject("courses", courseService.getCourseDtos());
        mav.addObject("professors", courseService.getProfessors());
        mav.addObject("classrooms", courseService.getClassrooms());
        return mav;
    }

    @PostMapping(value = "/")
    public ModelAndView hideExcessCourses(@ModelAttribute HideAndSeek user, @AuthenticationPrincipal UserDetails currentUser) {
        var mav = new ModelAndView("calendar");
        userService.updateCoursesVisibility(user);
        mav.addObject("loggedInUser", userService.getLoggedInUser(currentUser.getUsername()));
        mav.addObject("hideAndSeek", userService.getCoursesVisibility(user.getUserName()));
        mav.addObject("courses", courseService.getCourseDtos());
        mav.addObject("professors", courseService.getProfessors());
        mav.addObject("classrooms", courseService.getClassrooms());
        return mav;
    }

    @GetMapping("/classroom/{classroom}")
    public ModelAndView classroomCalendar(@PathVariable("classroom") String classroom, @AuthenticationPrincipal UserDetails currentUser) {
        var mav = new ModelAndView("calendar");
        if (currentUser != null) {
            mav.addObject("loggedInUser", userService.getLoggedInUser(currentUser.getUsername()));
        }
        mav.addObject("classroomNumber", classroom);
        mav.addObject("courses", courseService.getCourseDtos());
        mav.addObject("professors", courseService.getProfessors());
        mav.addObject("classrooms", courseService.getClassrooms());
        return mav;
    }

    @GetMapping("/professor/{professor}")
    public ModelAndView professorCalendar(@PathVariable("professor") String professor, @AuthenticationPrincipal UserDetails currentUser) {
        var mav = new ModelAndView("calendar");
        if (currentUser != null) {
            mav.addObject("loggedInUser", userService.getLoggedInUser(currentUser.getUsername()));
        }
        mav.addObject("professorName", professor);
        mav.addObject("courses", courseService.getCourseDtos());
        mav.addObject("professors", courseService.getProfessors());
        mav.addObject("classrooms", courseService.getClassrooms());
        return mav;
    }

    @GetMapping("/course/{courseValue}")
    public ModelAndView eachCourseCalendar(@PathVariable("courseValue") String courseValue, @AuthenticationPrincipal UserDetails currentUser) {
        var mav = new ModelAndView("calendar");
        if (currentUser != null) {
            mav.addObject("loggedInUser", userService.getLoggedInUser(currentUser.getUsername()));
        }
        mav.addObject("courseValue", courseValue);
        mav.addObject("courses", courseService.getCourseDtos());
        mav.addObject("professors", courseService.getProfessors());
        mav.addObject("classrooms", courseService.getClassrooms());
        return mav;
    }

    @GetMapping("/list/{semester}")
    public ModelAndView listOfCourses(@AuthenticationPrincipal UserDetails currentUser, @PathVariable("semester") String semester, @RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size) {
        int currentPage = Objects.requireNonNullElse(page, 1);
        int pageSize = Objects.requireNonNullElse(size, 5);
        var mav = new ModelAndView("listCourses");
        Page<CourseList> listOfCourses;
        int totalPages = 0;
        if (currentUser != null) {
            mav.addObject("loggedInUser", userService.getLoggedInUser(currentUser.getUsername()));
            listOfCourses = courseService.getListOfCoursesBySemester(currentUser.getUsername(), semester, PageRequest.of(currentPage - 1, pageSize));
            mav.addObject("listOfCourses", listOfCourses);
            totalPages = listOfCourses.getTotalPages();
        }
        var orders = generateSort();
        mav.addObject("semester", semester);
        mav.addObject("courses", courseService.getCourseDtos());
        mav.addObject("professors", courseService.getProfessors());
        mav.addObject("classrooms", courseService.getClassrooms());
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
            mav.addObject("pageNumbers", pageNumbers);
        }
        return mav;
    }

    private List<Sort.Order> generateSort() {
        List<Sort.Order> orders = new ArrayList<>();

        Sort.Order order1 = new Sort.Order(Sort.Direction.ASC, "numericDay");
        orders.add(order1);

        Sort.Order order2 = new Sort.Order(Sort.Direction.ASC, "start");
        orders.add(order2);

        return orders;
    }
}