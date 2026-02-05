package com.ballx.infra.social.response;

import com.ballx.constants.ProviderType;
import com.ballx.domain.dto.response.social.SocialTokenInfoResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenResponse(
	@JsonProperty("access_token")
	String accessToken,

	@JsonProperty("token_type")
	String tokenType,

	@JsonProperty("refresh_token")
	String refreshToken,

	@JsonProperty("expires_in")
	Integer expiresIn,

	@JsonProperty("refresh_token_expires_in")
	Integer refreshTokenExpiresIn,

	@JsonProperty("scope")
	String scope
) {
	public SocialTokenInfoResponse toSocialTokenInfo(ProviderType provider) {
		return new SocialTokenInfoResponse(provider, accessToken);
	}
}