package com.ballx.infra.oauth.naver;

import com.ballx.config.properties.oauth.NaverOauthProperties;
import com.ballx.constants.ProviderType;
import com.ballx.infra.client.base.BaseApiClient;

import com.ballx.infra.oauth.SocialApiClient;

import com.ballx.infra.oauth.dto.request.naver.NaverTokenRequest;

import com.ballx.infra.oauth.dto.response.common.SocialAccessTokenResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class NaverApiClient extends BaseApiClient implements SocialApiClient {

	private final NaverOauthProperties properties;

	public NaverApiClient(RestClient restClient, NaverOauthProperties properties) {
		super(restClient);
		this.properties = properties;
	}

	@Override
	public ProviderType getProviderType() {
		return ProviderType.NAVER;
	}

	// todo ProviderId API 요청 (for naver)
	@Override
	public String getProviderId(String code) {
		return "";
	}

	@Override
	public String getAccessToken(String code, String state) {
		NaverTokenRequest request = new NaverTokenRequest(
			properties.clientId(),
			properties.clientSecret(),
			properties.redirectUri(),
			code,
			state
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
