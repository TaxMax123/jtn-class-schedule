package jtn.classSchedule.backend.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class RecurringEvent extends Event {
    @JsonProperty("daysOfWeek")
    private Iterable<Integer> daysOfWeek;
    @JsonProperty("startTime")
    private String startTime;
    @JsonProperty("endTime")
    private String endTime;
    @JsonProperty("startRecur")
    private String startRecur;
    @JsonProperty("endRecur")
    private String endRecur;
}
