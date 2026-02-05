package com.ballx.infra.social.response;

import com.ballx.constants.ProviderType;
import com.ballx.domain.dto.response.social.SocialTokenInfoResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public record NaverTokenResponse(
	@JsonProperty("access_token")
	String accessToken,

	@JsonProperty("token_type")
	String tokenType,

	@JsonProperty("refresh_token")
	String refreshToken,

	@JsonProperty("expires_in")
	Integer expiresIn
) {
	public SocialTokenInfoResponse toSocialTokenInfo(ProviderType provider) {
		return new SocialTokenInfoResponse(provider, accessToken);
	}
}