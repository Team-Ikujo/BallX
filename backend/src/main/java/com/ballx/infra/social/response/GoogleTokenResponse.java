package com.ballx.infra.social.response;

import com.ballx.constants.ProviderType;
import com.ballx.domain.dto.response.social.SocialTokenInfoResponse;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleTokenResponse(
	@JsonProperty("access_token")
	String accessToken,

	@JsonProperty("token_type")
	String tokenType,

	@JsonProperty("expires_in")
	Integer expiresIn,

	@JsonProperty("scope")
	String scope,

	@JsonProperty("refresh_token")
	String refreshToken,

	@JsonProperty("id_token")
	String idToken
) {
	public SocialTokenInfoResponse toSocialTokenInfo(ProviderType provider) {
		return new SocialTokenInfoResponse(provider, accessToken);
	}
}