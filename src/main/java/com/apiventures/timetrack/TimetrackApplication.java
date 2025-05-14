package com.apiventures.timetrack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TimetrackApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimetrackApplication.class, args);
	}

}
