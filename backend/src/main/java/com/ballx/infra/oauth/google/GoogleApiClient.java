package com.ballx.infra.oauth.google;

import com.ballx.config.properties.oauth.GoogleOauthProperties;
import com.ballx.constants.ProviderType;
import com.ballx.infra.client.base.BaseApiClient;

import com.ballx.infra.oauth.SocialApiClient;

import com.ballx.infra.oauth.dto.request.google.GoogleTokenRequest;

import com.ballx.infra.oauth.dto.response.common.SocialAccessTokenResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class GoogleApiClient extends BaseApiClient implements SocialApiClient {

	private final GoogleOauthProperties properties;

	public GoogleApiClient(RestClient restClient, GoogleOauthProperties properties) {
		super(restClient);
		this.properties = properties;
	}

	@Override
	public ProviderType getProviderType() {
		return ProviderType.GOOGLE;
	}

	// todo ProviderId API 요청 (for google)
	@Override
	public String getProviderId(String code) {
		return "";
	}

	@Override
	public String getAccessToken(String code, String state) {
		String originCode = URLDecoder.decode(code, StandardCharsets.UTF_8);
		GoogleTokenRequest request = new GoogleTokenRequest(
			properties.clientId(),
			properties.clientSecret(),
			properties.redirectUri(),
			originCode
		);

		SocialAccessTokenResponse response = post(
			properties.tokenUrl(),
			request.toFormData(),
			MediaType.APPLICATION_FORM_URLENCODED,
			SocialAccessTokenResponse.class
		);

		return response.accessToken();
	}

}
