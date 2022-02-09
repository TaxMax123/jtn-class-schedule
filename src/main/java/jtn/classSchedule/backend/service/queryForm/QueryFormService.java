package jtn.classSchedule.backend.service.queryForm;

import jtn.classSchedule.backend.api.dto.ClassroomFilterResult;
import jtn.classSchedule.backend.api.dto.QueryFormChoice;
import jtn.classSchedule.backend.api.dto.QueryFormOptions;
import org.springframework.beans.support.PagedListHolder;
import org.springframework.stereotype.Service;

@Service
public interface QueryFormService {
    QueryFormOptions generateQueryFormOptions();

    PagedListHolder<ClassroomFilterResult> filterPages(QueryFormChoice choice, Integer page);
}
