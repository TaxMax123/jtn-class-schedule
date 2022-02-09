package jtn.classSchedule.backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HideAndSeek {
    private String userName;
    private Boolean allCoursesShown;
}
