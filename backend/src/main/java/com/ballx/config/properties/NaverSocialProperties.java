package com.ballx.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.naver")
public record NaverSocialProperties(
	String clientId,
	String clientSecret,
	String redirectUri,
	String authorizationGrantType,
	Provider provider
) {
	public record Provider(
		String authorizationUri,
		String tokenUri,
		String userInfoUri,
		String userNameAttribute
	) {
	}
}