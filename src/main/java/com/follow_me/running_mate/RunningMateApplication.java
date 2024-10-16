package com.follow_me.running_mate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RunningMateApplication {

	public static void main(String[] args) {
		SpringApplication.run(RunningMateApplication.class, args);
	}

}
