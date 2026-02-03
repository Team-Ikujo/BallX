package com.ballx.domain.entity.oauth;

import java.util.Map;

import com.ballx.constants.OAuthProvider;

public interface OAuth2UserInfo {

	OAuthProvider getProvider();

	String getAccessToken();

	Map<String, Object> getAttributes();

	String getId();

	String getEmail();

	String getName();

	String getGender();

}
