package com.ballx.domain.dto.response.oauth;

import com.ballx.constants.OAuth2Provider;

public record OAuth2LinkResponse(
	String message,
	OAuth2Provider provider,
	String email
) {
	public static OAuth2LinkResponse of(OAuth2Provider provider, String email) {
		return new OAuth2LinkResponse(
			"소셜 계정 연동이 완료되었습니다.",
			provider,
			email
		);
	}
}