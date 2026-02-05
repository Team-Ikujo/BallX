package com.ballx.infra.oauth;

import org.springframework.stereotype.Component;

import com.ballx.constants.OAuth2Provider;
import com.ballx.exception.OAuth2AuthenticationProcessingException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2UserUnlinkManager {

	private final GoogleOAuth2UserUnlink googleOAuth2UserUnlink;
	private final KakaoOAuth2UserUnlink kakaoOAuth2UserUnlink;
	private final NaverOAuth2UserUnlink naverOAuth2UserUnlink;

	public void unlink(OAuth2Provider provider, String accessToken) {
		if (OAuth2Provider.GOOGLE.equals(provider)) {
			googleOAuth2UserUnlink.unlink(accessToken);
		} else if (OAuth2Provider.KAKAO.equals(provider)) {
			kakaoOAuth2UserUnlink.unlink(accessToken);
		} else if (OAuth2Provider.NAVER.equals(provider)) {
			naverOAuth2UserUnlink.unlink(accessToken);
		} else {
			throw new OAuth2AuthenticationProcessingException(
				provider.getRegistrationId() + " 은(는) 지원하지 않습니다.");
		}
	}
}
