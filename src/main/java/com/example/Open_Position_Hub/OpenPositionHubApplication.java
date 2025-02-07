package com.example.Open_Position_Hub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class OpenPositionHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenPositionHubApplication.class, args);

    }

}
