package com.ballx.domain.dto.response.social;

import com.ballx.constants.ProviderType;

public record SocialCallbackResponse(
	ProviderType provider,
	String code
) {
	public static SocialCallbackResponse of(ProviderType provider, String code) {
		return new SocialCallbackResponse(provider, code);
	}
}