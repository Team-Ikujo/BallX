package com.ballx.infra.oauth;

import org.springframework.stereotype.Component;

import com.ballx.constants.ProviderType;
import com.ballx.exception.OAuth2AuthenticationProcessingException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2UserUnlinkManager {

	private final GoogleOAuth2UserUnlink googleOAuth2UserUnlink;
	private final KakaoOAuth2UserUnlink kakaoOAuth2UserUnlink;
	private final NaverOAuth2UserUnlink naverOAuth2UserUnlink;

	public void unlink(ProviderType provider, String accessToken) {
		if (ProviderType.GOOGLE.equals(provider)) {
			googleOAuth2UserUnlink.unlink(accessToken);
		} else if (ProviderType.KAKAO.equals(provider)) {
			kakaoOAuth2UserUnlink.unlink(accessToken);
		} else if (ProviderType.NAVER.equals(provider)) {
			naverOAuth2UserUnlink.unlink(accessToken);
		} else {
			throw new OAuth2AuthenticationProcessingException(
				provider.getRegistrationId() + " 은(는) 지원하지 않습니다.");
		}
	}
}
