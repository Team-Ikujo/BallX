package com.ballx.domain.entity.dto.response.oauth;

import java.util.Map;

import com.ballx.constants.OAuth2Provider;

public class GoogleOAuth2UserInfo implements OAuth2UserInfo {
	private final Map<String, Object> attributes;
	private final String id;
	private final String email;
	private final String name;

	public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
		this.attributes = attributes;
		this.id = attributes.get("sub").toString();
		this.email = attributes.get("email").toString();
		this.name = attributes.get("name") != null ? attributes.get("name").toString() : null;
	}

	@Override
	public OAuth2Provider getProvider() {
		return OAuth2Provider.GOOGLE;
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
