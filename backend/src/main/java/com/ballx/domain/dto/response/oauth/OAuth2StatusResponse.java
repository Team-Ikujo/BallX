package com.ballx.domain.dto.response.oauth;

import com.ballx.constants.AuthStatus;
import com.ballx.constants.ProviderType;

public record OAuth2StatusResponse(
	AuthStatus status,
	ProviderType provider,
	String email,
	String message,
	String mobile
) {
	public static OAuth2StatusResponse needSignup(
		ProviderType provider,
		String email
	) {
		return new OAuth2StatusResponse(
			AuthStatus.NEED_SIGNUP,
			provider,
			email,
			"회원 가입이 필요합니다",
			null
		);
	}

	public static OAuth2StatusResponse needMobileVerification(
		ProviderType provider,
		String email
	) {
		return new OAuth2StatusResponse(
			AuthStatus.NEED_MOBILE_VERIFICATION,
			provider,
			email,
			"휴대폰 인증이 필요합니다",
			null
		);
	}

	public static OAuth2StatusResponse login(
		ProviderType provider,
		String email,
		String mobile
	) {
		return new OAuth2StatusResponse(
			AuthStatus.LOGIN_SUCCESS,
			provider,
			email,
			"로그인 성공",
			mobile
		);
	}
}
