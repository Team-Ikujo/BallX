package com.ballx.config.properties.social;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.naver")
public record NaverSocialProperties(
	String clientId,
	String clientSecret,
	String redirectUri,
	String tokenUrl,
	String userInfoUrl
) {
}