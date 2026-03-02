package com.momentor.mentors;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MentorsApplication {
	public static void main(String[] args) {
		SpringApplication.run(MentorsApplication.class, args);
	}
}
