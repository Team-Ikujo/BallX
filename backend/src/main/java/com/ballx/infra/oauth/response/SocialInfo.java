package com.ballx.infra.oauth.response;

import java.util.Map;

import com.ballx.constants.ProviderType;

public interface SocialInfo {

	ProviderType getProvider();

	String getAccessToken();

	Map<String, Object> getAttributes();

	String getProviderId();

	String getEmail();

	String getName();

}
