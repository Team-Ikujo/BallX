package com.ballx.infra.oauth.dto.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SocialAccessTokenResponse(
	@JsonProperty("access_token")
	String accessToken
) {
}
