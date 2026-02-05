package com.ballx.config.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@ConfigurationProperties(prefix = "app.oauth2")
public record OAuth2Properties(
	@NotEmpty(message = "기본 리다이렉트 URI는 필수입니다")
	String defaultRedirectUri,

	List<String> authorizedRedirectUris
) {

	public OAuth2Properties {
		if (authorizedRedirectUris == null) {
			authorizedRedirectUris = new ArrayList<>();
		}

		if (!authorizedRedirectUris.contains(defaultRedirectUri)) {
			authorizedRedirectUris = new ArrayList<>(authorizedRedirectUris);
			authorizedRedirectUris.add(defaultRedirectUri);
		}

		log.info("   OAuth2 Properties Loaded");
		log.info("   Default Redirect URI: {}", defaultRedirectUri);
		log.info("   Authorized URIs: {}", authorizedRedirectUris);
	}

	public boolean isAuthorizedRedirectUri(String uri) {
		if (uri == null || uri.isBlank()) {
			return false;
		}

		return authorizedRedirectUris.stream()
			.anyMatch(authorizedUri ->
				uri.equals(authorizedUri) ||
					uri.startsWith(authorizedUri) ||
					matchesPattern(uri, authorizedUri)
			);
	}

	private boolean matchesPattern(String uri, String pattern) {
		if (!pattern.contains("*")) {
			return false;
		}

		String regex = pattern
			.replace(".", "\\.")
			.replace("*", ".*");
		return uri.matches(regex);
	}
}