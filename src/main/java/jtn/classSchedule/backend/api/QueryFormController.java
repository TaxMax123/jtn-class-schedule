package jtn.classSchedule.backend.api;

import jtn.classSchedule.backend.api.dto.FormSessionDto;
import jtn.classSchedule.backend.api.dto.QueryFormChoice;
import jtn.classSchedule.backend.api.dto.QueryFormOptions;
import jtn.classSchedule.backend.service.queryForm.QueryFormService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
@Component
@RequestMapping("/form")
public class QueryFormController {

    private final QueryFormService queryFormService;
    private final QueryFormOptions queryFormOptions;

    private final Map<String, FormSessionDto> formSessions = new HashMap<>();

    public QueryFormController(QueryFormService queryFormService) {
        this.queryFormService = queryFormService;
        this.queryFormOptions = queryFormService.generateQueryFormOptions();
    }

    @GetMapping("/query")
    public ModelAndView queryForm(HttpSession httpSession) {
        String sid = httpSession.getId();
        FormSessionDto sessionData = new FormSessionDto();
        sessionData.setMailBody("""

                Start time: %s:%s
                End time: %s:%s
                Start date: %s
                End date: %s
                Day of week: %s

                Lecturer: <enter lecturer>
                Lecture type: <enter lecture type>
                Course name: <enter course name>
                    """);
        formSessions.put(sid, sessionData);

        QueryFormChoice queryFormChoice = new QueryFormChoice();
        var mav = new ModelAndView("queryForm");
        mav.addObject("options", queryFormOptions);
        mav.addObject("choice", queryFormChoice);
        mav.addObject("showTable", false);
        return mav;
    }

    @GetMapping(value = "/query", params = "action=filter")
    public ModelAndView queryFilter(@ModelAttribute QueryFormChoice choice,
                                    HttpSession httpSession) {
        var sid = httpSession.getId();
        var data = formSessions.get(sid);
        data.setQueryFormChoice4page(choice);
        var mav = new ModelAndView("queryForm");
        mav.addObject("options", queryFormOptions);
        mav.addObject("choice", choice);
        mav.addObject("showTable", true);
        data.setFilterPages(queryFormService.filterPages(choice, 0));
        mav.addObject("classroomPages", data.getFilterPages().getPageList());
        data.setTotalPages(data.getFilterPages().getPageCount());
        if (data.getTotalPages() > 0) {
            data.setPageNumbers(IntStream.rangeClosed(1, data.getTotalPages())
                    .boxed()
                    .collect(Collectors.toList()));
        }
        mav.addObject("totalPages", data.getTotalPages());
        mav.addObject("pageNumbers", data.getPageNumbers());
        var a = SecurityContextHolder.getContext().getAuthentication();
        mav.addObject("auth", a);
        mav.addObject("mailBody", getMailBody(data));
        return mav;
    }

    @GetMapping(value = "/query", params = "action=page")
    private ModelAndView queryFilterPage(@RequestParam(value = "page", required = false) Integer page,
                                         HttpSession httpSession, @ModelAttribute QueryFormChoice choice) {
        var sid = httpSession.getId();
        var data = formSessions.get(sid);
        var mav = new ModelAndView("queryForm");
        mav.addObject("options", queryFormOptions);
        mav.addObject("choice", data.getQueryFormChoice4page());
        mav.addObject("showTable", true);
        data.setFilterPages(queryFormService.filterPages(data.getQueryFormChoice4page(), page));
        mav.addObject("classroomPages", data.getFilterPages().getPageList());
        mav.addObject("totalPages", data.getTotalPages());
        mav.addObject("pageNumbers", data.getPageNumbers());
        var a = SecurityContextHolder.getContext().getAuthentication();
        mav.addObject("auth", a);
        mav.addObject("mailBody", getMailBody(data));
        return mav;
    }

    private String getMailBody(FormSessionDto data) {
        return String.format(data.getMailBody(),
                        data.getQueryFormChoice4page().startTimeHour, data.getQueryFormChoice4page().startTimeMinute,
                        data.getQueryFormChoice4page().endTimeHour, data.getQueryFormChoice4page().endTimeHour,
                        data.getQueryFormChoice4page().startEvent,
                        data.getQueryFormChoice4page().endEvent,
                        data.getQueryFormChoice4page().getDayOfWeek())
                .replace("\n", "%0A")
                .replace("                ", "");
    }
}
