package jtn.classSchedule.backend.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    private String start;
    private String finish;
    private String color;
    private String description;
    private String title;
    private String lecturer;
    private String lectureType;
    private Integer semester;
    private String classroom;
    private Integer classroomCapacity;
    private String weekday;
    @JsonProperty("startStr")
    private String startStr;
    @JsonProperty("endStr")
    private String endStr;
    private Boolean editable = false;
    private Boolean eventDurationEditable = false;
    private Boolean eventStartEditable = false;
}

