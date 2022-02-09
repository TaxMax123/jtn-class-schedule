package jtn.classSchedule.backend.api.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ReportDto {
    public final Map<String, ArrayList<ReportDateTimeDto>> report = new LinkedHashMap<>();
}

