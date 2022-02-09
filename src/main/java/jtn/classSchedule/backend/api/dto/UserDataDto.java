package jtn.classSchedule.backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
@Builder
public class UserDataDto {
    private final Integer role;
    private final Integer year;
    private final String firstName;
    private final String lastName;
}
