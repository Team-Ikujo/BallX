package com.ballx.domain.entity.oauth;

import java.util.Map;

import com.ballx.constants.OAuthProvider;
import com.ballx.exception.OAuth2AuthenticationProcessingException;

public class OAuthInfoFactory {

	public static OAuth2UserInfo getOAuth2UserInfo(
		String registrationId, String accessToken, Map<String, Object> attributes) {
		if (OAuthProvider.GOOGLE.getRegistrationId().equals(registrationId)) {
			return new GoogleOAuth2UserInfo(accessToken, attributes);
		} else if (OAuthProvider.KAKAO.getRegistrationId().equals(registrationId)) {
			return new KakaoOAuth2UserInfo(accessToken, attributes);
		} else if (OAuthProvider.NAVER.getRegistrationId().equals(registrationId)) {
			return new NaverOAuth2UserInfo(accessToken, attributes);
		} else {
			throw new OAuth2AuthenticationProcessingException(
				registrationId + "은(는) 지원하지 않습니다.");
		}
	}
}
