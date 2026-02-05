package com.ballx.domain.dto.response.oauth;

public record OAuth2TokenResponse(
	String accessToken,
	String tokenType,
	Long expiresIn
) {
	public static OAuth2TokenResponse of(String accessToken, Long expiresIn) {
		return new OAuth2TokenResponse(accessToken, "Bearer", expiresIn);
	}
}