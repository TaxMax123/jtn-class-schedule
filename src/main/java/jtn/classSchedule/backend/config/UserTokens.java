package jtn.classSchedule.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;


@Component
public class UserTokens {
    @Value("${user.access.tokens}")
    private ArrayList<String> tokens;

    private Boolean exists(String token) {
        for (var t : tokens) {
            if (t.equals(token)) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<String> getTokens() {
        return new ArrayList<>(tokens);
    }

    public Boolean removeUserToken(String token) {
        return tokens.remove(token);
    }

    public Boolean removeAllUserTokens() {
        try {
            tokens.clear();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean addUserToken(String token) {
        if (exists(token)) {
            return false;
        }
        return tokens.add(token);
    }

    public Boolean contains(String auth) {
        for (var t : tokens) {
            if (auth.equals(String.format("Bearer %s", t))) {
                return true;
            }
        }
        return false;
    }
}
