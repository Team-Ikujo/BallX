package com.ballx.domain.entity.dto.response.oauth;

import com.ballx.constants.OAuth2Provider;

public interface OAuth2UserInfo {

	OAuth2Provider getProvider();

	String getProviderId();

	String getEmail();

	String getName();

}
