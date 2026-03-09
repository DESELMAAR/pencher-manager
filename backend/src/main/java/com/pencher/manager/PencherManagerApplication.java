package com.pencher.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PencherManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(PencherManagerApplication.class, args);
    }
}
