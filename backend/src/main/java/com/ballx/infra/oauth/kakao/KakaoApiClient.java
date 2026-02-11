package com.ballx.infra.oauth.kakao;

import com.ballx.config.properties.oauth.KakaoOauthProperties;
import com.ballx.constants.ProviderType;
import com.ballx.infra.client.base.BaseApiClient;

import com.ballx.infra.oauth.SocialApiClient;
import com.ballx.infra.oauth.dto.request.kakao.KakaoTokenRequest;
import com.ballx.infra.oauth.dto.response.common.SocialAccessTokenResponse;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class KakaoApiClient extends BaseApiClient implements SocialApiClient {

	private final KakaoOauthProperties properties;

	public KakaoApiClient(RestClient restClient, KakaoOauthProperties properties) {
		super(restClient);
		this.properties = properties;
	}

	@Override
	public ProviderType getProviderType() {
		return ProviderType.KAKAO;
	}

	// todo ProviderId API 요청 (for kakao)
	@Override
	public String getProviderId(String code) {
		return "";
	}

	@Override
	public String getAccessToken(String code, String state) {
		KakaoTokenRequest request = new KakaoTokenRequest(
			properties.clientId(),
			properties.clientSecret(),
			properties.redirectUri(),
			code
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
