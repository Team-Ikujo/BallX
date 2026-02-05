package com.ballx.domain.dto.response.oauth;

import java.util.Map;

import com.ballx.constants.OAuth2Provider;

public class KakaoOAuth2UserInfo implements OAuth2UserInfo {
	private final String accessToken;
	private final Map<String, Object> attributes;
	private final String id;
	private final String email;
	private final String name;

	public KakaoOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
		this.accessToken = accessToken;
		// kakaoAccount 에 attributes Map이 할당되어 있음
		Map<String, Object> kakaoAccount = (Map<String, Object>)attributes.get("kakao_account");

		this.attributes = (Map<String, Object>)kakaoAccount.get("profile");

		this.id = ((Long)attributes.get("id")).toString();
		this.email = attributes.get("email").toString();

		this.name = null;

		this.attributes.put("id", id);
		this.attributes.put("email", this.email);
	}

	@Override
	public OAuth2Provider getProvider() {
		return OAuth2Provider.KAKAO;
	}

	@Override
	public String getAccessToken() {
		return accessToken;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public String getProviderId() {
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

}
