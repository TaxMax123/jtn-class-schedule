package jtn.classSchedule.backend.api.dto;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Getter
public class QueryFormOptions {
    private final List<Integer> START_HOUR = IntStream.rangeClosed(8, 20).boxed().toList();
    private final List<Integer> START_MINUTE = IntStream.iterate(0, i -> i + 15).limit(4).boxed().toList();
    private final List<Integer> END_HOUR = IntStream.rangeClosed(9, 21).boxed().toList();
    private final List<Integer> END_MINUTE = IntStream.iterate(0, i -> i + 15).limit(4).boxed().toList();
    private final List<String> DAYS_OF_WEEK = Arrays.asList("Pon", "Uto", "Sri", "Cet", "Pet", "Sub");
}