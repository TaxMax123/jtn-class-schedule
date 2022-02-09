package jtn.classSchedule.backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Semester {
    private String firstSemesterStart;
    private String firstSemesterEnd;
    private String secondSemesterStart;
    private String secondSemesterEnd;
}
