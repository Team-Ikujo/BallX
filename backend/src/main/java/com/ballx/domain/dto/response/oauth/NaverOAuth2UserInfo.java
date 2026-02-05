package com.ballx.domain.dto.response.oauth;

import java.util.Map;

import com.ballx.constants.ProviderType;

public class NaverOAuth2UserInfo implements OAuth2UserInfo {
	private final Map<String, Object> attributes;
	private final String accessToken;
	private final String id;
	private final String email;
	private final String name;

	public NaverOAuth2UserInfo(String accessToken, Map<String, Object> attributes) {
		this.accessToken = accessToken;
		this.attributes = (Map<String, Object>)attributes.get("response");
		this.id = this.attributes.get("id").toString();
		this.email = this.attributes.get("email").toString();
		this.name = this.attributes.get("name").toString();
	}

	@Override
	public ProviderType getProvider() {
		return ProviderType.NAVER;
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
