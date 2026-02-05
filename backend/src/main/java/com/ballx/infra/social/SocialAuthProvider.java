package com.ballx.infra.social;

import com.ballx.domain.dto.response.social.SocialTokenInfoResponse;

public interface SocialAuthProvider {
	SocialTokenInfoResponse getAccessToken(String code);

	String getProviderName();
}