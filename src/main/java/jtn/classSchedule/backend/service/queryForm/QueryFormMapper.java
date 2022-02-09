package jtn.classSchedule.backend.service.queryForm;

import jtn.classSchedule.backend.api.dto.*;
import jtn.classSchedule.backend.persistence.course.Course;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface QueryFormMapper {
    @Mappings({
            @Mapping(target = "start", expression = "java(choice.getStartTimeHour() + \":\" + choice.getStartTimeMinute())"),
            @Mapping(target = "end", expression = "java(choice.getEndTimeHour() + \":\" + choice.getEndTimeMinute())")
    })
    ClassroomDto entityToDto(QueryFormChoice choice);

    ArrayList<CoursePostDto> entityToCoursePostDto(ArrayList<Course> courses);

    @Mappings({
            @Mapping(target = "title", expression = "java(courses.getShortName() + \" \" + courses.getLectureType())"),
            @Mapping(target = "description", source = "courses.name"),
            @Mapping(target = "startStr", source = "courses.startTime"),
            @Mapping(target = "endStr", source = "courses.endTime")
    })
    @IterableMapping(elementTargetType = RecurringEvent.class)
    ArrayList<RecurringEvent> coursesToRecurringEvents(ArrayList<Course> courses);

    @Mappings({
            @Mapping(target = "title", expression = "java(course.getShortName() + \" \" + course.getLectureType())"),
            @Mapping(target = "description", source = "course.name"),
            @Mapping(target = "startStr", source = "course.startTime"),
            @Mapping(target = "endStr", source = "course.endTime")
    })
    @IterableMapping(elementTargetType = RecurringEvent.class)
    RecurringEvent courseToRecurringEvent(Course course);

    @Mappings({
            @Mapping(target = "name", source = "recurringEvent.classroom"),
            @Mapping(target = "capacity", source = "recurringEvent.classroomCapacity")
    })
    @IterableMapping(elementTargetType = ClassroomFilterResult.class)
    ClassroomFilterResult recurringEventToClassroomFilterResult(RecurringEvent recurringEvent);

    @Mappings({
            @Mapping(target = "name", source = "recurringEvents.classroom"),
            @Mapping(target = "capacity", source = "recurringEvents.classroomCapacity")
    })
    @IterableMapping(elementTargetType = ClassroomFilterResult.class)
    ArrayList<ClassroomFilterResult> recurringEventsToClassroomFilterResults(List<RecurringEvent> recurringEvents);

}
