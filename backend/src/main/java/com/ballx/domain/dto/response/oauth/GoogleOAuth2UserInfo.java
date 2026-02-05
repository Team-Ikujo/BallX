package com.ballx.domain.dto.response.oauth;

import java.util.Map;

import com.ballx.constants.ProviderType;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
	private final String accessToken;
	private final Map<String, Object> attributes;
	private final String id;
	private final String email;
	private final String name;

	public GoogleOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
		this.accessToken = accessToken;
		this.attributes = attributes;
		this.id = attributes.get("sub").toString();
		this.email = attributes.get("email").toString();
		this.name = attributes.get("name") != null ? attributes.get("name").toString() : null;
	}

	@Override
	public ProviderType getProvider() {
		return ProviderType.GOOGLE;
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
