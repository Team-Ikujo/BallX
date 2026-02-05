package com.ballx.security;

public final class SecurityPath {

	public static final String[] PUBLIC = {
		"/oauth2/redirect",

		// OAuth2 Provider
		"/auth/*/callback",
		"/auth/google/callback",
		"/auth/kakao/callback",
		"/auth/naver/callback",

		// 소셜로그인
		"/api/v1/auth/*/login",
		"/api/v1/auth/google/login",
		"/api/v1/auth/kakao/login",
		"/api/v1/auth/naver/login",
	};

}
