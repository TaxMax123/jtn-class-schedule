package jtn.classSchedule.backend.service.user;

import jtn.classSchedule.backend.api.dto.HideAndSeek;
import jtn.classSchedule.backend.api.dto.UserDto;
import jtn.classSchedule.backend.persistence.user.User;
import jtn.classSchedule.backend.persistence.user.UserRepository;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Service("jtnUserService")
public class UserServiceImpl implements UserService {

    private final UserMapper MAPPER = Mappers.getMapper(UserMapper.class);
    private final UserRepository userRepository;
    @Autowired
    @Lazy
    private BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(@Qualifier("userRepository") UserRepository userRepository) {
        super();
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findUserByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
        if (user.getRole() == 1) {
            return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPwd(), mapRolesToAuthorities("ROLE_ADMIN"));
        }
        if (user.getRole() == 2) {
            return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPwd(), mapRolesToAuthorities("ROLE_MODERATOR"));
        }
        return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPwd(), mapRolesToAuthorities("ROLE_USER"));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(String role) {
        var authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority(role));
        return authorities;
    }

    @Override
    public UserDto getLoggedInUser(String userName) {
        var entity = userRepository.findUserByUserName(userName);
        return entity != null ? MAPPER.entityToLoggedInUser(entity) : null;
    }

    @Override
    public HideAndSeek getCoursesVisibility(String userName) {
        var entity = userRepository.findUserByUserName(userName);
        return entity != null ? MAPPER.entityToHideAndSeek(entity) : null;
    }

    @Override
    public void updateCoursesVisibility(HideAndSeek user) {
        var entity = userRepository.findUserByUserName(user.getUserName());
        entity.setAllCoursesShown(!user.getAllCoursesShown());
        userRepository.save(entity);
    }
}
