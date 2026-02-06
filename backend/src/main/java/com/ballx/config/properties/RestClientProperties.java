package com.ballx.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "rest-client")
public record RestClientProperties(
	int connectTimeoutSeconds,
	int readTimeoutSeconds
) {}