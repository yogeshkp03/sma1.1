package com.smartmeal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartMealAutopilotApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SmartMealAutopilotApplication.class, args);
    }
}
