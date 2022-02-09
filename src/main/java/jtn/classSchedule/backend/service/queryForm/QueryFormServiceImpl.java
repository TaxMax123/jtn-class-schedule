package jtn.classSchedule.backend.service.queryForm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jtn.classSchedule.backend.api.dto.*;
import jtn.classSchedule.backend.persistence.course.Course;
import jtn.classSchedule.backend.persistence.course.CourseRepository;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.support.PagedListHolder;
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
@AllArgsConstructor
public class QueryFormServiceImpl implements QueryFormService {
    private static final QueryFormMapper MAPPER = Mappers.getMapper(QueryFormMapper.class);
    private final CourseRepository courseRepository;

    @Override
    public QueryFormOptions generateQueryFormOptions() {
        return new QueryFormOptions();
    }

    @Override
    public PagedListHolder<ClassroomFilterResult> filterPages(QueryFormChoice choice, Integer page) {
        var defaultPage = page == null ? 0 : page;
        var size = 3;
        var formatChosenStartHour = parseHour(choice.startTimeHour, choice.startTimeMinute);
        var formatChosenEndHour = parseHour(choice.endTimeHour, choice.endTimeMinute);

        var result = getRecurringEvents();
        List<RecurringEvent> filtered = result.stream().filter(r -> calendarTimeOverlap(r, choice, formatChosenStartHour, formatChosenEndHour)).toList();

        var classrooms = new HashSet<ClassroomFilterResult>();
        if (choice.minSize == null) {
            filtered.forEach(event -> classrooms.add(MAPPER.recurringEventToClassroomFilterResult(event)));
        } else {
            filtered.stream().filter(event -> hasAdequateCapacity(event, choice.minSize)).toList()
                    .forEach(event -> classrooms.add(MAPPER.recurringEventToClassroomFilterResult(event)));
        }
        var list = classrooms.stream().toList();
        PagedListHolder<ClassroomFilterResult> pages = new PagedListHolder<>(list);
        pages.setPageSize(size);
        pages.setPage(defaultPage);
        return pages;
    }

    private Boolean hasAdequateCapacity(RecurringEvent recurringEvent, Integer capacity) {
        return recurringEvent.getClassroomCapacity() >= capacity;
    }

    private ArrayList<RecurringEvent> getRecurringEvents() {
        var courses = courseRepository.findAll();
        var courseList = new ArrayList<Course>();
        courses.forEach(courseList::add);
        var rec = MAPPER.coursesToRecurringEvents(courseList);
        inspectProperDateTime(rec);
        setStartAndFinish(rec);
        return rec;
    }

    private LocalTime parseHour(Integer hour, Integer minute) {
        var hourFormat = hour < 10 ? ("0" + hour) : hour.toString();
        var minuteFormat = minute < 10 ? ("0" + minute) : minute.toString();
        return LocalTime.parse(hourFormat + ":" + minuteFormat);
    }

    private Boolean calendarTimeOverlap(RecurringEvent event, QueryFormChoice compareTo, LocalTime chosenStartHour, LocalTime chosenEndHour) {
        var eventStartHour = LocalTime.parse(event.getStart().substring(11, 16));
        var eventEndHour = LocalTime.parse(event.getStart().substring(11, 16));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m");
        LocalDateTime eventStartNew = LocalDateTime.parse(
                compareTo.getStartEvent().toString() + " " + compareTo.getStartTimeHour().toString() + ":" + compareTo.getStartTimeMinute().toString(),
                dateTimeFormatter);
        LocalDateTime eventEndNew = LocalDateTime.parse(
                compareTo.getEndEvent().toString() + " " + compareTo.getEndTimeHour().toString() + ":" + compareTo.getEndTimeMinute().toString(),
                dateTimeFormatter);
        LocalDateTime eventStartRec = LocalDateTime.parse(event.getStartRecur());
        LocalDateTime eventEndRec = LocalDateTime.parse(event.getEndRecur());

        if (eventStartNew.isAfter(eventEndNew) || chosenStartHour.isAfter(chosenEndHour)) {
            return false;
        }

        return !((eventStartNew.equals(eventStartRec) ||
                (eventStartNew.isAfter(eventStartRec) && eventEndNew.isBefore(eventEndRec)) ||
                (eventStartNew.isBefore(eventStartRec) && eventEndNew.isAfter(eventStartRec)) ||
                (eventStartNew.isAfter(eventStartRec) && eventStartNew.isBefore(eventEndRec) && eventEndNew.isAfter(eventEndRec)) ||
                (eventStartNew.isBefore(eventStartRec) && eventEndNew.isAfter(eventEndRec))) &&
                (eventStartHour.equals(chosenStartHour) ||
                        (chosenStartHour.isAfter(eventStartHour)) && chosenEndHour.isBefore(eventEndHour) ||
                        (chosenStartHour.isBefore(eventStartHour) && chosenEndHour.isAfter(eventStartHour)) ||
                        (chosenStartHour.isAfter(eventStartHour)) && chosenStartHour.isBefore(eventEndHour) && chosenEndHour.isAfter(eventEndHour) ||
                        (chosenStartHour.isBefore(eventStartHour) && chosenEndHour.isAfter(eventEndHour))));
    }

    private void setStartAndFinish(ArrayList<RecurringEvent> recurringEvent) {
        for (RecurringEvent e : recurringEvent) {
            var s = e.getStartStr();
            var f = e.getEndStr();
            e.setStart(getDate(s).toString());
            e.setFinish(getDate(f).toString());
        }
    }

    private LocalDateTime getDate(String hour) {
        var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (hour == null) {
            return LocalDateTime.now();
        }
        String hourFormat;
        if (hour.equals("8:15") || hour.equals("9:15")) {
            hourFormat = " 0" + hour + ":00";
        } else {
            hourFormat = " " + hour + ":00";
        }
        String stringDate = "2022-01-10" + hourFormat;
        return LocalDateTime.parse(stringDate, formatter);
    }

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

    private void setDaysOfWeek(RecurringEvent recurringEvent) {
        var daysOfWeek = new String[]{"Ned", "Pon", "Uto", "Sri", "Cet", "Pet", "Sub"};
        int index = 0;
        var daysOfWeekMap = new HashMap<Integer, String>();
        for (String day : daysOfWeek) {
            daysOfWeekMap.put(index, day);
            index++;
        }
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
        int a, b, c, d;
        a = b = c = d = 0;

        if (today.compareTo(LocalDateTime.parse(currentYear + semester.getSecondSemesterStart())) < 0) {
            a = -1;
        } else if (today.compareTo(LocalDateTime.parse(currentYear + semester.getSecondSemesterStart())) > 0) {
            if (today.getMonthValue() < 7) {
                a = -1;
            } else {
                b = c = d = 1;
            }
        }
        var startOfFirstSemester = currentYear + a + semester.getFirstSemesterStart();
        var endOfFirstSemester = currentYear + b + semester.getFirstSemesterEnd();
        var startOfSecondSemester = currentYear + c + semester.getSecondSemesterStart();
        var endOfSecondSemester = currentYear + d + semester.getSecondSemesterEnd();

        recurringEvents.stream()
                .filter(e -> e.getSemester() % 2 == 1)
                .forEach(e -> setRecurringEvent(e, startOfFirstSemester, endOfFirstSemester));

        recurringEvents.stream()
                .filter(e -> e.getSemester() % 2 == 0)
                .forEach(e -> setRecurringEvent(e, startOfSecondSemester, endOfSecondSemester));
    }
}
