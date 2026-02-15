package com.ballx.domain.dto.response.social;

import com.ballx.constants.ProviderType;

public record SocialTokenInfoResponse(
	ProviderType provider,
	String accessToken
) {
}