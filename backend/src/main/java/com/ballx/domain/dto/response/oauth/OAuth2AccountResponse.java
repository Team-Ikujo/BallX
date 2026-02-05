package com.ballx.domain.dto.response.oauth;

import java.time.Instant;

import com.ballx.constants.OAuth2Provider;

public record OAuth2AccountResponse(
	OAuth2Provider provider,
	String email,
	Instant linkedAt
) {
}
