package com.ballx.config.oauth;

import java.util.Collections;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ballx.constants.ProviderType;
import com.ballx.constants.UserRole;
import com.ballx.domain.dto.response.oauth.GoogleOAuth2UserInfo;
import com.ballx.domain.dto.response.oauth.KakaoOAuth2UserInfo;
import com.ballx.domain.dto.response.oauth.NaverOAuth2UserInfo;
import com.ballx.domain.dto.response.oauth.OAuth2UserInfo;
import com.ballx.exception.OAuth2AuthenticationProcessingException;
import com.ballx.security.OAuth2UserPrincipal;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oAuth2User = super.loadUser(userRequest);

		// 현재 로그인 진행중인 소셜 구분
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		String accessToken = userRequest.getAccessToken().getTokenValue();
		Map<String, Object> attributes = oAuth2User.getAttributes();

		OAuth2UserInfo userinfo = getOAuth2UserInfo(registrationId, accessToken, attributes);

		return new OAuth2UserPrincipal(
			userinfo,
			Collections.singleton(new SimpleGrantedAuthority("ROLE_" + UserRole.MEMBER.name()))
		);

	}

	private OAuth2UserInfo getOAuth2UserInfo(
		String registrationId,
		String accessToken, Map<String,
			Object> attributes
	) {
		if (ProviderType.GOOGLE.getRegistrationId().equals(registrationId)) {
			return new GoogleOAuth2UserInfo(accessToken, attributes);
		} else if (ProviderType.KAKAO.getRegistrationId().equals(registrationId)) {
			return new KakaoOAuth2UserInfo(accessToken, attributes);
		} else if (ProviderType.NAVER.getRegistrationId().equals(registrationId)) {
			return new NaverOAuth2UserInfo(accessToken, attributes);
		} else {
			throw new OAuth2AuthenticationProcessingException(
				registrationId + "은(는) 지원하지 않습니다.");
		}
	}
}
