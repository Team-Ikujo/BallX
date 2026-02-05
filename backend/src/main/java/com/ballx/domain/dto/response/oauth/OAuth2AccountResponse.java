package com.ballx.domain.dto.response.oauth;

import java.time.Instant;

import com.ballx.constants.ProviderType;

public record OAuth2AccountResponse(
	ProviderType provider,
	String email,
	Instant linkedAt
) {
}
