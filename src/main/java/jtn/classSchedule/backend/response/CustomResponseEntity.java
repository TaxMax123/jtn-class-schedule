package jtn.classSchedule.backend.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class CustomResponseEntity {
    private final Integer responseCode;
    private final String responsePhrase;
    private final String description;
}
