package jtn.classSchedule.backend.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Getter
@Setter
@NoArgsConstructor
public class QueryFormChoice {
    public Integer startTimeHour;
    public Integer startTimeMinute;
    public Integer endTimeHour;
    public Integer endTimeMinute;
    public String dayOfWeek;
    public Integer minSize;
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    public LocalDate startEvent;
    @DateTimeFormat(pattern = "MM/dd/yyyy")
    public LocalDate endEvent;
}
