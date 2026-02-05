package com.ballx.domain.dto.response.oauth;

import com.ballx.constants.ProviderType;

// OAuth2 정보를 담는 내부 레코드
public record OAuth2InfoResponse(
	ProviderType provider,
	String providerId,
	String email,
	String accessToken
) {
}