package com.ballx.security;

public final class SecurityPath {

	public static final String[] PUBLIC = {
		"/error",
		"/favicon.ico",

		// oauth2 provider
		"/api/google/auth/redirect",
		"/api/kakao/auth/redirect",
		"/api/naver/auth/redirect",

		// OAuth2 인증 필요 없는 엔드포인트
		"/api/v1/oauth2/callback",
		"/api/v1/oauth2/signup",
		"/api/v1/oauth2/verify-phone",
		"/api/v1/oauth2/link-account"
	};

	private SecurityPath() {
		throw new UnsupportedOperationException("Utility class");
	}
}
