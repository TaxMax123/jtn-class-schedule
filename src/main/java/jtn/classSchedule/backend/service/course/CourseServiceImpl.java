package jtn.classSchedule.backend.service.course;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jtn.classSchedule.backend.api.dto.*;
import jtn.classSchedule.backend.persistence.course.Course;
import jtn.classSchedule.backend.persistence.course.CourseRepository;
import jtn.classSchedule.backend.service.user.UserService;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Component("courseServiceImplementation")
@AllArgsConstructor
public class CourseServiceImpl implements CourseService {
    private static final CourseMapper MAPPER = Mappers.getMapper(CourseMapper.class);
    private static final Comparator<CourseDto> LexicographicalComparatorCourseDto = (o1, o2) -> {
        var course1 = o1.name();
        var course2 = o2.name();

        return course1.compareTo(course2);
    };
    private static final Comparator<Classroom> LexicographicalComparatorClassroom = (o1, o2) -> {
        var classroom1 = o1.classroom();
        var classroom2 = o2.classroom();

        return classroom1.compareTo(classroom2);
    };
    private static final Comparator<Professor> LexicographicalComparatorProfessor = (p1, p2) -> {
        var professor1 = p1.professorName();
        var professor2 = p2.professorName();

        return professor1.compareTo(professor2);
    };
    private static final Comparator<Course> StartTimeComparator = (o1, o2) -> {
        var startTime1 = o1.getStartTime();
        var startTime2 = o2.getStartTime();

        if ("8:15".equals(startTime1)) {
            return -1;
        }
        if ("8:15".equals(startTime2)) {
            return 1;
        }
        if ("9:15".equals(startTime1)) {
            return -1;
        }
        if ("9:15".equals(startTime2)) {
            return 1;
        }
        return startTime1.compareTo(startTime2);
    };
    @Qualifier("courseRepository")
    private final CourseRepository courseRepository;
    private final UserService userService;

    private Semester getSemester() {
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

    private LocalDateTime getDate(String hour) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String hourFormat;
        if (hour.equals("8:15") || hour.equals("9:15")) {
            hourFormat = " 0" + hour + ":00";
        } else {
            hourFormat = " " + hour + ":00";
        }
        String stringDate = "2022-01-10" + hourFormat;
        return LocalDateTime.parse(stringDate, formatter);
    }

    private HashMap<Integer, String> getDaysNumericValue() {
        var daysOfWeek = new String[]{"Ned", "Pon", "Uto", "Sri", "Cet", "Pet", "Sub"};
        int index = 0;
        var daysOfWeekMap = new HashMap<Integer, String>();
        for (String day : daysOfWeek) {
            daysOfWeekMap.put(index, day);
            index++;
        }
        return daysOfWeekMap;
    }

    private void setDaysOfWeek(RecurringEvent recurringEvent) {
        var daysOfWeekMap = getDaysNumericValue();
        for (Map.Entry<Integer, String> entry : daysOfWeekMap.entrySet()) {
            if (entry.getValue().equals(recurringEvent.getWeekday())) {
                var dow = new ArrayList<Integer>();
                dow.add(entry.getKey());
                recurringEvent.setDaysOfWeek(dow);
            }
        }
    }

    private void setRecurringEvent(RecurringEvent recurringEvent, String startOfSemester, String endOfSemester) {
        recurringEvent.setStartRecur(startOfSemester);
        recurringEvent.setEndRecur(endOfSemester);
        var lt = recurringEvent.getLectureType();
        String color = switch (lt) {
            case "LV" -> "green";
            case "P" -> "blue";
            case "S" -> "red";
            default -> "pink";
        };
        Boolean editable = lt.equals("LV");

        recurringEvent.setEventStartEditable(editable);
        recurringEvent.setColor(color);
        setDaysOfWeek(recurringEvent);
    }

    private void inspectProperDateTime(ArrayList<RecurringEvent> recurringEvents) {
        var semester = getSemester();
        if (semester == null) {
            return;
        }
        var today = LocalDateTime.now();
        var currentYear = today.getYear();
//        int a, b, c, d; a = b = c = d = 0;
        int indexA, indexB;
        indexA = indexB = 0;
        if (today.compareTo(LocalDateTime.parse(currentYear + semester.getSecondSemesterStart())) < 0) {
            //first semester after new years. // set data for 10. - 12. month of last year.
            indexA = -1;
        } else if (today.compareTo(LocalDateTime.parse(currentYear + semester.getSecondSemesterStart())) > 0) {
            //second semester or the end of academic year
            //if semester is still active
            if (today.getMonthValue() < 7) {
                indexA = -1;
            } else {
                indexB = 1;
            }
        }
        var startOfFirstSemester = currentYear + indexA + semester.getFirstSemesterStart();
        var endOfFirstSemester = currentYear + indexB + semester.getFirstSemesterEnd();
        var startOfSecondSemester = currentYear + indexB + semester.getSecondSemesterStart();
        var endOfSecondSemester = currentYear + indexB + semester.getSecondSemesterEnd();

        recurringEvents.stream()
                .filter(e -> e.getSemester() % 2 == 1)
                .forEach(e -> setRecurringEvent(e, startOfFirstSemester, endOfFirstSemester));

        recurringEvents.stream()
                .filter(e -> e.getSemester() % 2 == 0)
                .forEach(e -> setRecurringEvent(e, startOfSecondSemester, endOfSecondSemester));
    }

    private void setStartAndFinish(ArrayList<RecurringEvent> recurringEvent) {
        for (RecurringEvent e : recurringEvent) {
            var s = e.getStartStr();
            var f = e.getEndStr();
            e.setStart(getDate(s).toString());
            e.setFinish(getDate(f).toString());
        }
    }

    private Course loadInitialAscendingCourse() {
        var coursesAscending = courseRepository.findAll(Sort.by(Sort.Direction.ASC, "courseProgramShortName_s", "courseYear_i"));
        return coursesAscending.iterator().next();
    }

    private ArrayList<RecurringEvent> initialEventNonLoggedIn() {
        var initialDisplayEvent = loadInitialAscendingCourse();
        if (initialDisplayEvent == null) {
            return null;
        }
        var courses = courseRepository.findCoursesByProgramShortNameAndYear(
                initialDisplayEvent.getProgramShortName(),
                initialDisplayEvent.getYear()
        );
        return MAPPER.coursesToRecurringEvents(courses);
    }

    private ArrayList<RecurringEvent> getAllViewableEvents(ArrayList<String> enrolledCourses, ArrayList<Course> courses) {
        var studentEnrolledCourses = new ArrayList<Course>();
        var courseIdSet = new HashSet<String>();
        enrolledCourses.forEach(enrolled -> studentEnrolledCourses.addAll(courses.stream().filter(entity -> isEnrolledCourse(enrolled, entity)).toList()));
        studentEnrolledCourses.forEach(c -> courseIdSet.add(c.getId()));
        var allViewableEvents = new ArrayList<RecurringEvent>();
        courseIdSet.forEach(id -> allViewableEvents.addAll(MAPPER.coursesToRecurringEvents(courseRepository.findCoursesById(id))));
        return allViewableEvents;
    }

    private ArrayList<RecurringEvent> filterStudentsView(UserDto user) {
        var studentEnrolledCourses = new ArrayList<RecurringEvent>();
        var list = new ArrayList<Course>();
        courseRepository.findAll().forEach(list::add);
        if (user.getAllCoursesShown()) {
            return getAllViewableEvents(user.getEnrolledCourses(), list);
        } else {
            user.getEnrolledCourses().forEach(enrolled -> studentEnrolledCourses.add(MAPPER.courseToRecurringEvent(courseRepository.findCourseByDocId(enrolled))));
            return studentEnrolledCourses;
        }
    }

    private ArrayList<RecurringEvent> personalizedEventLoggedIn(String userName) {
        var user = userService.getLoggedInUser(userName);
        if (user.getRole() == 2) {
            var name = user.getFirstName() + " " + user.getLastName();
            return user.getAllCoursesShown()
                    ? MAPPER.coursesToRecurringEvents(courseRepository.findCoursesByLecturerOrHolder(name, name))
                    : MAPPER.coursesToRecurringEvents(courseRepository.findCoursesByLecturer(name));
        } else if (user.getRole() == 3) {
            return filterStudentsView(user);
        }
        return initialEventNonLoggedIn();
    }

    @Override
    public ArrayList<RecurringEvent> getRecurringEvents(String... userName) {
        var recurringEvents = userName.length != 1 ? initialEventNonLoggedIn() : personalizedEventLoggedIn(userName[0]);
        inspectProperDateTime(recurringEvents);
        setStartAndFinish(recurringEvents);
        return recurringEvents;
    }

    private Boolean isEnrolledCourse(String enrolledCourse, Course course) {
        return enrolledCourse.equals(course.getDocId());
    }

    private Boolean isPartOfEnrolledCourse(String enrolledCourse, Course course) {
        return enrolledCourse.equals(course.getId());
    }

    private String concatenate(Course course) {
        var programShortName = course.getProgramShortName();
        var sub = programShortName.substring(1);
        return sub + " " + course.getYear() + ". godina";
    }

    @Override
    public ArrayList<CourseDto> getCourseDtos() {
        var courses = courseRepository.findAll();

        var coursesString = new HashSet<String>();
        courses.forEach(c -> coursesString.add(concatenate(c)));

        var courseDtos = new HashSet<CourseDto>();
        coursesString.forEach(course -> courseDtos.add(new CourseDto(course)));

        var list = new ArrayList<>(courseDtos);
        list.sort(LexicographicalComparatorCourseDto);
        return list;
    }

    @Override
    public ArrayList<RecurringEvent> getCoursesByCourseDto(String courseDtoName) {
        var programShortName = "S" + courseDtoName.split(" ")[0];
        var year = Integer.parseInt(courseDtoName.split(" ")[1].substring(0, 1));
        var courses = courseRepository.findCoursesByProgramShortNameAndYear(programShortName, year);
        var events = MAPPER.coursesToRecurringEvents(courses);
        inspectProperDateTime(events);
        setStartAndFinish(events);
        return events;
    }

    @Override
    public ArrayList<Classroom> getClassrooms() {
        var courses = getCourses();

        var classroomsString = new HashSet<String>();
        courses.forEach(c -> classroomsString.add(c.getClassroom()));

        var classrooms = new HashSet<Classroom>();
        classroomsString.forEach(classroom -> classrooms.add(new Classroom(classroom)));

        var list = new ArrayList<>(classrooms);
        list.sort(LexicographicalComparatorClassroom);
        return list;
    }

    @Override
    public ArrayList<RecurringEvent> getCoursesByClassroom(String classroom) {
        var courses = courseRepository.findCoursesByClassroom(classroom);
        var events = MAPPER.coursesToRecurringEvents(courses);
        inspectProperDateTime(events);
        setStartAndFinish(events);
        return events;
    }

    @Override
    public ArrayList<Professor> getProfessors() {
        var courses = getCourses();

        var professorsString = new HashSet<String>();
        courses.forEach(p -> professorsString.add(p.getLecturer()));

        var professors = new HashSet<Professor>();
        professorsString.forEach(professor -> professors.add(new Professor(professor)));

        var list = new ArrayList<>(professors);
        list.sort(LexicographicalComparatorProfessor);
        return list;
    }

    @Override
    public ArrayList<RecurringEvent> getCoursesByProfessor(String professor) {
        var courses = courseRepository.findCoursesByLecturer(professor);
        var events = MAPPER.coursesToRecurringEvents(courses);
        inspectProperDateTime(events);
        setStartAndFinish(events);
        return events;
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

    private List<Course> filterListBySemester(List<Course> courses, String semester) {
        return semester.equals("winter") ? courses.stream().filter(c -> c.getSemester() % 2 != 0).toList() : courses.stream().filter(c -> c.getSemester() % 2 == 0).toList();
    }

    private Page<CourseList> generatePage(List<CourseList> events, Pageable pageable) {
        List<CourseList> list;
        if (events.size() < pageable.getPageNumber() * pageable.getPageSize()) {
            list = Collections.emptyList();
        } else {
            int toIndex = Math.min(pageable.getPageNumber() * pageable.getPageSize() + pageable.getPageSize(), events.size());
            list = events.subList(pageable.getPageNumber() * pageable.getPageSize(), toIndex);
        }
        return new PageImpl<>(list, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort()), events.size());
    }

    private List<CourseList> setDaysToNumbers(List<CourseList> courses) {
        var numericDaysValue = getDaysNumericValue();
        courses.forEach(c -> numericDaysValue.forEach((k, v) -> {
            if (c.getDay().equalsIgnoreCase(v)) {
                c.setNumericDay(k);
            }
        }));
        return courses;
    }

    @Override
    public Page<CourseList> getListOfCoursesBySemester(String userName, String semester, Pageable pageable) {
        var user = userService.getLoggedInUser(userName);
        if (user.getRole() == 2) {
            var name = user.getFirstName() + " " + user.getLastName();
            var events = setDaysToNumbers(MAPPER.courseListToCourseListDtos(filterListBySemester(courseRepository.findCoursesByLecturerOrHolder(name, name), semester)));
            events.sort(new CourseList.WeekdayComparator().thenComparing(new CourseList.TimeComparator()));
            return generatePage(events, pageable);
        } else {
            var list = new ArrayList<Course>();
            courseRepository.findAll().forEach(list::add);
            var studentEnrolledCourses = new HashSet<Course>();
            var courseIdSet = new HashSet<String>();
            user.getEnrolledCourses().forEach(enrolled -> studentEnrolledCourses.addAll(list.stream().filter(entity -> isEnrolledCourse(enrolled, entity)).toList()));
            studentEnrolledCourses.forEach(c -> courseIdSet.add(c.getId()));
            courseIdSet.forEach(id -> studentEnrolledCourses.addAll(list.stream().filter(cid -> isPartOfEnrolledCourse(id, cid)).toList()));
            var events = setDaysToNumbers(MAPPER.courseListToCourseListDtos(filterListBySemester(studentEnrolledCourses.stream().toList(), semester)));
            events.sort(new CourseList.WeekdayComparator().thenComparing(new CourseList.TimeComparator()));
            return generatePage(events, pageable);
        }
    }

    @Override
    public ArrayList<Course> getCourses() {
        var iterable = courseRepository.findAll();
        var courses = new ArrayList<Course>();
        iterable.forEach(courses::add);

        courses.sort(StartTimeComparator);
        return courses;
    }

    @Override
    public Course getCourseById(String id) {
        return courseRepository.findCourseByDocId(id);
    }

    @Override
    public void processRecEvents(ArrayList<RecurringEvent> recurringEventArrayList) {
        inspectProperDateTime(recurringEventArrayList);
        setStartAndFinish(recurringEventArrayList);
    }
}
