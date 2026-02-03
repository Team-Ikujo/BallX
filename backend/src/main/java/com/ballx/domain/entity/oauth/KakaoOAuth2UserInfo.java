package com.ballx.domain.entity.oauth;

import java.util.Map;

import com.ballx.constants.OAuthProvider;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {
	private final Map<String, Object> attributes;
	private final String accessToken;
	private final String id;
	private final String email;
	private final String name;
	private final String gender;

	public KakaoOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
		this.accessToken = accessToken;
		// kakaoAccount 에 attributes Map이 할당되어 있음
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");
		Map<String, Object> kakaoProfile = (Map<String, Object>)kakaoAccount.get("profile");
		this.attributes = kakaoProfile;

		this.id = ((Long)attributes.get("id")).toString();
		this.email = attributes.get("email").toString();

		this.name = null;
		this.gender = null;

		this.attributes.put("id", id);
		this.attributes.put("email", this.email);
	}

	@Override
	public OAuthProvider getProvider() {
		return OAuthProvider.KAKAO;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getAccessToken() {
		return accessToken;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getGender() {
		return gender;
	}
}
