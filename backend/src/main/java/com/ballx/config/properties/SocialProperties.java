package com.ballx.config.properties;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.security.oauth2.client")
public record SocialProperties(
	Map<String, Registration> registration,
	Map<String, Provider> provider
) {
	public record Registration(
		String clientId,
		String clientSecret,
		String redirectUri,
		String authorizationGrantType,
		String clientAuthenticationMethod
	) {
	}

	public record Provider(
		String authorizationUri,
		String tokenUri,
		String userInfoUri,
		String userNameAttribute
	) {
	}

	public Registration getRegistration(String provider) {
		return this.registration.get(provider);
	}

	public Provider getProvider(String provider) {
		return this.provider.get(provider);
	}
}