package com.ballx.domain.dto.response;

public class AuthResponse {
	public record LoginResponse(
		String accessToken
	) {
		public static LoginResponse of(String accessToken) {
			return new LoginResponse(accessToken);
		}
	}
}