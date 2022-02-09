package jtn.classSchedule.backend.service.course;

import jtn.classSchedule.backend.api.dto.*;
import jtn.classSchedule.backend.persistence.course.Course;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "courseMapper", builder = @Builder(disableBuilder = true))
public interface CourseMapper {

    @Mappings({
            @Mapping(target = "title", expression = "java(course.getShortName() + \" \" + course.getLectureType())"),
            @Mapping(target = "description", source = "course.name"),
            @Mapping(target = "startStr", source = "course.startTime"),
            @Mapping(target = "endStr", source = "course.endTime")
    })
    Event courseToEvent(Course course);

    @Mappings({
            @Mapping(target = "title", expression = "java(courses.getShortName() + \" \" + courses.getLectureType())"),
            @Mapping(target = "description", source = "courses.name"),
            @Mapping(target = "startStr", source = "courses.startTime"),
            @Mapping(target = "endStr", source = "courses.endTime")
    })
    @IterableMapping(elementTargetType = Event.class)
    ArrayList<Event> coursesToEvents(ArrayList<Course> courses);

    @Mappings({
            @Mapping(target = "title", expression = "java(course.getShortName() + \" \" + course.getLectureType())"),
            @Mapping(target = "description", source = "course.name"),
            @Mapping(target = "startStr", source = "course.startTime"),
            @Mapping(target = "endStr", source = "course.endTime")
    })
    @IterableMapping(elementTargetType = RecurringEvent.class)
    RecurringEvent courseToRecurringEvent(Course course);

    @Mappings({
            @Mapping(target = "title", expression = "java(courses.getShortName() + \" \" + courses.getLectureType())"),
            @Mapping(target = "description", source = "courses.name"),
            @Mapping(target = "startStr", source = "courses.startTime"),
            @Mapping(target = "endStr", source = "courses.endTime")
    })
    @IterableMapping(elementTargetType = RecurringEvent.class)
    ArrayList<RecurringEvent> coursesToRecurringEvents(ArrayList<Course> courses);

    Course postDtoToCourse(CoursePostDto coursePostDto);

    CoursePostDto courseToPostDto(Course course);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCourseFromDto(CoursePostDto coursePostDto, @MappingTarget Course course);

    Classroom courseToClassroom(Course course);

    @Mappings({
            @Mapping(target = "courseId", source = "course.id"),
            @Mapping(target = "start", source = "course.startTime"),
            @Mapping(target = "end", source = "course.endTime"),
            @Mapping(target = "day", source = "course.weekday")
    })
    CourseList courseToCourseListDto(Course course);

    @Mappings({
            @Mapping(target = "courseId", source = "courses.id"),
            @Mapping(target = "start", source = "courses.startTime"),
            @Mapping(target = "end", source = "courses.endTime"),
            @Mapping(target = "day", source = "courses.weekday")
    })
    List<CourseList> courseListToCourseListDtos(List<Course> courses);
}
