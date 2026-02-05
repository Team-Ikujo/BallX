package com.ballx.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderType {
	GOOGLE("google"),
	KAKAO("kakao"),
	NAVER("naver");

	private final String registrationId;
}