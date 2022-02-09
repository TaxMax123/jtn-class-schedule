package jtn.classSchedule.backend.service.report;

import jtn.classSchedule.backend.api.dto.ReportDateTimeDto;
import jtn.classSchedule.backend.api.dto.ReportDto;
import jtn.classSchedule.backend.persistence.course.Course;
import jtn.classSchedule.backend.persistence.course.CourseRepository;
import jtn.classSchedule.backend.service.course.CourseMapper;
import jtn.classSchedule.backend.service.course.CourseService;
import jtn.classSchedule.backend.service.systemAdmin.AdminService;
import lombok.AllArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class ReportServiceImpl implements ReportService {
    private static final CourseMapper MAPPER = Mappers.getMapper(CourseMapper.class);
    private static final Comparator<String> weekdayComparator = (o1, o2) -> {
        Integer i1 = switch (o1) {
            case "Pon" -> 1;
            case "Uto" -> 2;
            case "Sri" -> 3;
            case "Cet" -> 4;
            case "Pet" -> 5;
            case "Sub" -> 6;
            case "Ned" -> 0;
            default -> 7;
        };
        Integer i2 = switch (o2) {
            case "Pon" -> 1;
            case "Uto" -> 2;
            case "Sri" -> 3;
            case "Cet" -> 4;
            case "Pet" -> 5;
            case "Sub" -> 6;
            case "Ned" -> 0;
            default -> 7;
        };
        return Integer.compare(i1, i2);
    };
    private final AdminService adminService;
    private final CourseService courseService;
    private final CourseRepository courseRepository;

    private Boolean isAuth(String auth) {
        return auth != null && !auth.isEmpty() && !auth.isBlank() && (adminService.getUserTokens().contains(auth.split(" ")[1]) || auth.equals(String.format("Bearer %s", adminService.getToken())));
    }

    @Override
    public ReportDto fullEventReportBy(String by, String auth) {
        if (!isAuth(auth)) {
            return null;
        }
        ReportDto fullReport = new ReportDto();
        ArrayList<Course> allEvents = new ArrayList<>();
        courseRepository.findAll().forEach(allEvents::add);
        var allRecEvents = MAPPER.coursesToRecurringEvents(allEvents);
        courseService.processRecEvents(allRecEvents);
        ArrayList<String> mapKeys = new ArrayList<>();
        HashMap<String, ArrayList<ReportDateTimeDto>> report = new HashMap<>();

        allRecEvents.forEach(event -> {
            ReportDateTimeDto reportDateTimeDto = new ReportDateTimeDto();
            reportDateTimeDto.setStartDate(event.getStartRecur().split("T")[0]);
            reportDateTimeDto.setEndDate(event.getEndRecur().split("T")[0]);
            reportDateTimeDto.setStartTime(event.getStartTime());
            reportDateTimeDto.setEndTime(event.getEndTime());
            String key = "noFilter";
            if (by != null && by.equals("classroom")) {
                key = event.getClassroom();
            } else if (by != null && by.equals("weekday")) {
                key = event.getWeekday();
            }
            if (report.containsKey(key)) {
                report.get(key).add(reportDateTimeDto);
            } else {
                mapKeys.add(key);
                var data = new ArrayList<ReportDateTimeDto>();
                data.add(reportDateTimeDto);
                report.put(key, data);
            }
        });
        if (by != null && by.equals("classroom")) {
            mapKeys.sort(String::compareTo);
        } else if (by != null && by.equals("weekday")) {
            mapKeys.sort(weekdayComparator);
        }
        mapKeys.forEach(key -> fullReport.report.put(key, report.get(key)));
        for (String key : fullReport.report.keySet().stream().toList()) {
            fullReport.report.get(key).sort(Comparator.comparing(Object::toString));
        }
        return fullReport;
    }

}
