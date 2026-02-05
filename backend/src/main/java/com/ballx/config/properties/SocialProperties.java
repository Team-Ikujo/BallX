package com.ballx.config.properties;

import java.util.Map;
import java.util.Set;

public record SocialProperties(
	Map<String, Provider> provider,
	Map<String, Registration> registration
) {
	public record Registration(
		String authorizationGrantType,
		String clientAuthenticationMethod,
		String clientId,
		String clientName,
		String clientSecret,
		String redirectUri,
		Set<String> scope
	) {
	}

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
