package com.ballx.config.properties;

import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.naver")
public record NaverSocialProperties(
	String authorizationGrantType,
	String clientAuthenticationMethod,
	String clientId,
	String clientName,
	String clientSecret,
	String redirectUri,
	Set<String> scope
) {
	public record Provider(
		String authorizationUri,
		String issuerUri,
		String jwkSetUri,
		String tokenUri,
		String UserInfoAuthenticationMethod,
		String userInfoUri,
		String userNameAttribute
	) {
	}
}