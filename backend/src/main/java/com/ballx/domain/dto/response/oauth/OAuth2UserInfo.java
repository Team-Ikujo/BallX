package com.ballx.domain.dto.response.oauth;

import java.util.Map;

import com.ballx.constants.ProviderType;

public interface OAuth2UserInfo {

	ProviderType getProvider();

	String getAccessToken();

	Map<String, Object> getAttributes();

	String getProviderId();

	String getEmail();

	String getName();

}
