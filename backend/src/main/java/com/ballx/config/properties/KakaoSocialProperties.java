package com.ballx.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.kakao")
public record KakaoSocialProperties(
	String clientId,
	String clientSecret,
	String redirectUri,
	String authorizationGrantType,
	String clientAuthenticationMethod,
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