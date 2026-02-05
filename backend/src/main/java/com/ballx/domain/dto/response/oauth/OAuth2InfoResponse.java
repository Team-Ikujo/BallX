package com.ballx.domain.dto.response.oauth;

import com.ballx.constants.OAuth2Provider;

// OAuth2 정보를 담는 내부 레코드
public record OAuth2InfoResponse(
	OAuth2Provider provider,
	String providerId,
	String email,
	String accessToken
) {
}