package com.ballx.domain.dto.response.oauth;

import com.ballx.constants.ProviderType;

public record OAuth2LinkResponse(
	String message,
	ProviderType provider,
	String email
) {
	public static OAuth2LinkResponse of(ProviderType provider, String email) {
		return new OAuth2LinkResponse(
			"소셜 계정 연동이 완료되었습니다.",
			provider,
			email
		);
	}
}