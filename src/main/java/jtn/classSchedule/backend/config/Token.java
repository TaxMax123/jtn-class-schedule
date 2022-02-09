package jtn.classSchedule.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Component
@EntityScan
@ComponentScan
@Configuration
public class Token {
    @Value("${admin.access.token}")
    private String token;

    @Override
    public String toString() {
        return token;
    }
}
