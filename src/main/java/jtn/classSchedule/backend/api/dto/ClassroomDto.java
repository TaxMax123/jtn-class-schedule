package jtn.classSchedule.backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassroomDto {
    private String name;
    private String start;
    private String end;
    private LocalDate startEvent;
    private LocalDate endEvent;
    private String dayOfWeek;
    private Integer capacity;
}
