package com.ballx.config.properties.social;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oauth.kakao")
public record KakaoSocialProperties(
	String clientId,
	String clientSecret,
	String redirectUri,
	String tokenUrl,
	String userInfoUrl
) {
}