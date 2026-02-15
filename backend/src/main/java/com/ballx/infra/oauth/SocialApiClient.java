package com.ballx.infra.oauth;

import com.ballx.constants.ProviderType;

public interface SocialApiClient {

	ProviderType getProviderType();

	String getAccessToken(String code, String state);

	String getProviderId(String accessToken);

}
