package jtn.classSchedule.backend.service.user;

import jtn.classSchedule.backend.api.dto.HideAndSeek;
import jtn.classSchedule.backend.api.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@Component
public interface UserService extends UserDetailsService {
    UserDto getLoggedInUser(String userName);

    HideAndSeek getCoursesVisibility(String userName);

    void updateCoursesVisibility(HideAndSeek user);
}
