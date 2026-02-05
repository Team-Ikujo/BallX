package com.ballx.infra.social;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.ballx.config.properties.GoogleSocialProperties;
import com.ballx.constants.ProviderType;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.domain.dto.response.social.SocialTokenInfoResponse;
import com.ballx.exception.CustomException;
import com.ballx.infra.social.response.GoogleTokenResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GoogleAuthProvider implements SocialAuthProvider {

	private final RestClient externalRestClient;
	private final GoogleSocialProperties googleProperties;

	@Override
	public SocialTokenInfoResponse getAccessToken(String code) {
		MultiValueMap<String, String> googleParams = new LinkedMultiValueMap<>();
		googleParams.add("grant_type", googleProperties.authorizationGrantType());
		googleParams.add("client_id", googleProperties.clientId());
		googleParams.add("client_secret", googleProperties.clientSecret());
		googleParams.add("redirect_uri", googleProperties.redirectUri());
		googleParams.add("code", code);

		GoogleTokenResponse googleResponse = externalRestClient.post()
			.uri("https://oauth2.googleapis.com/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(googleParams)
			.retrieve()
			.body(GoogleTokenResponse.class);

		if (googleResponse == null || googleResponse.accessToken() == null) {
			throw new CustomException(ErrorCode.SOCIAL_TOKEN_FAIL);
		}

		return googleResponse.toSocialTokenInfo(ProviderType.GOOGLE);
	}

	@Override
	public String getProviderName() {
		return ProviderType.GOOGLE.getProviderId();
	}
}