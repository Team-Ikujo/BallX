package com.ballx.infra.social;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import com.ballx.config.properties.KakaoSocialProperties;
import com.ballx.constants.ProviderType;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.domain.dto.response.social.SocialTokenInfoResponse;
import com.ballx.exception.CustomException;
import com.ballx.infra.social.response.KakaoTokenResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KakaoAuthProvider implements SocialAuthProvider {

	private final RestClient externalRestClient;
	private final KakaoSocialProperties kakaoProperties;

	@Override
	public SocialTokenInfoResponse getAccessToken(String code) {
		MultiValueMap<String, String> kakaoParams = new LinkedMultiValueMap<>();
		kakaoParams.add("grant_type", kakaoProperties.authorizationGrantType());
		kakaoParams.add("client_id", kakaoProperties.clientId());
		kakaoParams.add("client_secret", kakaoProperties.clientSecret());
		kakaoParams.add("redirect_uri", kakaoProperties.redirectUri());
		kakaoParams.add("code", code);
		
		KakaoTokenResponse kakaoResponse = externalRestClient.post()
			.uri("https://kauth.kakao.com/oauth/token")
			.contentType(MediaType.APPLICATION_FORM_URLENCODED)
			.body(kakaoParams)
			.retrieve()
			.body(KakaoTokenResponse.class);

		if (kakaoResponse == null || kakaoResponse.accessToken() == null) {
			throw new CustomException(ErrorCode.SOCIAL_TOKEN_FAIL);
		}

		return kakaoResponse.toSocialTokenInfo(ProviderType.KAKAO);
	}

	@Override
	public String getProviderName() {
		return ProviderType.KAKAO.getProviderId();
	}
}