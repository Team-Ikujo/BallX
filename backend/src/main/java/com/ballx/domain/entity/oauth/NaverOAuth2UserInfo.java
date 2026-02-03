package com.ballx.domain.entity.oauth;

import java.util.Map;

import com.ballx.constants.OAuthProvider;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {
	private final Map<String, Object> attributes;
	private final String accessToken;
	private final String id;
	private final String email;
	private final String name;
	private final String gender;

	public NaverOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
		this.attributes = (Map<String, Object>)attributes.get("response");
		this.accessToken = accessToken;
		this.id = this.attributes.get("id").toString();
		this.email = this.attributes.get("email").toString();
		this.name = this.attributes.get("name").toString();
		this.gender = this.attributes.get("gender").toString().toUpperCase();
	}

	@Override
	public OAuthProvider getProvider() {
		return OAuthProvider.NAVER;
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
