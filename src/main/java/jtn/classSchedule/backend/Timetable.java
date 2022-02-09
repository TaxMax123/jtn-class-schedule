package jtn.classSchedule.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;

@SpringBootApplication
public class Timetable {

    public static void main(String[] args) {

        String port = "8080";
        for (String arg : args) {
            if (arg.contains("-Dport=")) {
                try {
                    Integer.parseInt(arg.split("=")[1]);
                    port = arg.split("=")[1];
                    if (port.isEmpty() || port.isBlank()) {
                        port = "8080";
                    }
                } catch (Exception e) {
                    port = "8080";
                }
            }
        }
        SpringApplication app = new SpringApplication(Timetable.class);
        app.setDefaultProperties(Collections.singletonMap("server.port", port));
        app.run(args);
    }
}
