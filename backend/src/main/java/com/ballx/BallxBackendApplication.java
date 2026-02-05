package com.ballx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@ConfigurationPropertiesScan
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties
public class BallxBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(BallxBackendApplication.class, args);
	}
}
