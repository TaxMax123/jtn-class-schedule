package jtn.classSchedule.backend.api;

import jtn.classSchedule.backend.response.CustomResponseEntity;
import jtn.classSchedule.backend.service.report.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@Component
@RestController
@RequestMapping("/reports")
@AllArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping(value = "/events")
    public ResponseEntity<Object> fullEventReportBy(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam(value = "by", required = false) String by) {
        var data = reportService.fullEventReportBy(by, auth);
        if (data == null) {
            return new ResponseEntity<>(CustomResponseEntity.builder().responseCode(HttpStatus.UNAUTHORIZED.value()).responsePhrase(HttpStatus.UNAUTHORIZED.getReasonPhrase()).description("Access denied, not authorized").build(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @GetMapping(value = "/events", params = "classroom")
    public ResponseEntity<Object> filteredEventReportByClassroom(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam(value = "classroom") String classroom) {
        var data = reportService.fullEventReportBy("classroom", auth);
        if (data == null) {
            return new ResponseEntity<>(CustomResponseEntity.builder().responseCode(HttpStatus.UNAUTHORIZED.value()).responsePhrase(HttpStatus.UNAUTHORIZED.getReasonPhrase()).description("Access denied, not authorized").build(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(data.report.getOrDefault(classroom, new ArrayList<>()), HttpStatus.OK);
    }

    @GetMapping(value = "/events", params = "weekday")
    public ResponseEntity<Object> filteredEventReportByWeekday(
            @RequestHeader(value = "Authorization", required = false) String auth,
            @RequestParam(value = "weekday") String weekday) {
        var data = reportService.fullEventReportBy("weekday", auth);
        if (data == null) {
            return new ResponseEntity<>(CustomResponseEntity.builder().responseCode(HttpStatus.UNAUTHORIZED.value()).responsePhrase(HttpStatus.UNAUTHORIZED.getReasonPhrase()).description("Access denied, not authorized").build(), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(data.report.getOrDefault(weekday, new ArrayList<>()), HttpStatus.OK);
    }
}
