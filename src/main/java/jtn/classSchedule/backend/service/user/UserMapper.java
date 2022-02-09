package jtn.classSchedule.backend.service.user;

import jtn.classSchedule.backend.api.dto.HideAndSeek;
import jtn.classSchedule.backend.api.dto.RegistrationDto;
import jtn.classSchedule.backend.api.dto.UserDto;
import jtn.classSchedule.backend.persistence.user.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    User dtoToEntity(RegistrationDto user);

    UserDto entityToLoggedInUser(User user);

    HideAndSeek entityToHideAndSeek(User user);
}
