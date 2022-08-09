package com.greenart.movie_history;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MovieHistoryApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieHistoryApplication.class, args);
	}

}
