package jtn.classSchedule.backend.api.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.support.PagedListHolder;

import java.util.List;

@Getter
@Setter
public class FormSessionDto {
    private QueryFormChoice queryFormChoice4page;
    private PagedListHolder<ClassroomFilterResult> filterPages;
    private Integer totalPages;
    private List<Integer> pageNumbers;
    private String mailBody;
}
