package jtn.classSchedule.backend.service.report;

import jtn.classSchedule.backend.api.dto.ReportDto;

public interface ReportService {
    ReportDto fullEventReportBy(String by, String auth);
}
