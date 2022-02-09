package jtn.classSchedule.backend.api.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportDateTimeDto {
    String StartDate;
    String EndTime;
    String StartTime;
    String EndDate;

    @Override
    public String toString() {
        return StartDate + "-" + StartTime + "-" + EndTime + "-" + EndDate;
    }
}
