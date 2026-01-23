package com.ballx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class BallxBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BallxBackendApplication.class, args);
	}
}
