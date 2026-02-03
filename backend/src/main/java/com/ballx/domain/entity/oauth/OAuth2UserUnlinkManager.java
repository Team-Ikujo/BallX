package com.ballx.domain.entity.oauth;

import org.springframework.stereotype.Component;

import com.ballx.constants.OAuthProvider;
import com.ballx.exception.OAuth2AuthenticationProcessingException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2UserUnlinkManager {

	private GoogleOAuth2UserUnlink googleOAuth2UserUnlink;
	private KakaoOAuth2UserUnlink kakaoOAuth2UserUnlink;
	private NaverOAuth2UserUnlink naverOAuth2UserUnlink;

	public void unlink(OAuthProvider provider, String accessToken) {
		if (OAuthProvider.GOOGLE.equals(provider)) {
			googleOAuth2UserUnlink.unlink(accessToken);
		} else if (OAuthProvider.KAKAO.equals(provider)) {
			kakaoOAuth2UserUnlink.unlink(accessToken);
		} else if (OAuthProvider.NAVER.equals(provider)) {
			naverOAuth2UserUnlink.unlink(accessToken);
		} else {
			throw new OAuth2AuthenticationProcessingException(
				provider.getRegistrationId() + "은(는) 지원하지 않습니다");
		}
	}
}
