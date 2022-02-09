package jtn.classSchedule.backend.api;

import jtn.classSchedule.backend.service.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@Component
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    UserService userService;

    @GetMapping("/login")
    public ModelAndView loginMethod() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("login");
        return modelAndView;
    }
}
