package com.ballx.config.properties.social;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.google")
public record GoogleSocialProperties(
	String clientId,
	String clientSecret,
	String redirectUri,
	String tokenUrl,
	String userInfoUrl
) {
}