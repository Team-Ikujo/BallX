package com.ballx.domain.entity.dto.response.oauth;

import java.util.Map;

import com.ballx.constants.OAuth2Provider;

public interface OAuth2UserInfo {

	OAuth2Provider getProvider();

	String getAccessToken();

	Map<String, Object> getAttributes();

	String getProviderId();

	String getEmail();

	String getName();

}
