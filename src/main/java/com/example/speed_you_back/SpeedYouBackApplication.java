package com.example.speed_you_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling    		// SchedulerService를 위한 Annotation
public class SpeedYouBackApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeedYouBackApplication.class, args);
	}

}
