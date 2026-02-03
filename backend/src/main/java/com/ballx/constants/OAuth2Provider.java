package com.ballx.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {
	GOOGLE("구글"),
	KAKAO("카카오"),
	NAVER("네이버");

	private final String registrationId;
}