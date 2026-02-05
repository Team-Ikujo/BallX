package com.ballx.infra.social;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.ballx.config.properties.NaverSocialProperties;
import com.ballx.constants.ProviderType;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.domain.dto.response.social.SocialTokenInfoResponse;
import com.ballx.exception.CustomException;
import com.ballx.infra.social.response.NaverTokenResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NaverAuthProvider implements SocialAuthProvider {

	private final RestClient externalRestClient;
	private final NaverSocialProperties naverProperties;

	@Override
	public SocialTokenInfoResponse getAccessToken(String code) {
		MultiValueMap<String, String> naverParams = new LinkedMultiValueMap<>();
		naverParams.add("grant_type", naverProperties.authorizationGrantType());
		naverParams.add("client_id", naverProperties.clientId());
		naverParams.add("client_secret", naverProperties.clientSecret());
		naverParams.add("redirect_uri", naverProperties.redirectUri());
		naverParams.add("code", code);
		naverParams.add("state", "RANDOM_STATE"); // 네이버는 state 파라미터 필수

		NaverTokenResponse naverResponse = externalRestClient.post()
			.uri("https://nid.naver.com/oauth2.0/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(naverParams)
			.retrieve()
			.body(NaverTokenResponse.class);

		if (naverResponse == null || naverResponse.accessToken() == null) {
			throw new CustomException(ErrorCode.SOCIAL_TOKEN_FAIL);
		}

		return naverResponse.toSocialTokenInfo(ProviderType.NAVER);
	}

	@Override
	public String getProviderName() {
		return ProviderType.NAVER.getProviderId();
	}
}