package com.ballx.domain.entity.oauth;

import java.util.Map;

import com.ballx.constants.OAuthProvider;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
	private final Map<String, Object> attributes;
	private final String accessToken;
	private final String id;
	private final String email;
	private final String name;
	private final String gender;

	public GoogleOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
		this.attributes = attributes;
		this.accessToken = accessToken;
		this.id = attributes.get("sub").toString();
		this.email = attributes.get("email").toString();
		this.name = null;
		this.gender = null;
	}

	@Override
	public OAuthProvider getProvider() {
		return OAuthProvider.GOOGLE;
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
