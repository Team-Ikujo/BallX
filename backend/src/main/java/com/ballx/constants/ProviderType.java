package com.ballx.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderType {
	GOOGLE("google"),
	KAKAO("kakao"),
	NAVER("naver");

	private final String providerId;

	public static ProviderType upperProviderType(String provider) {
		try {
			return ProviderType.valueOf(provider.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException(
				("지원하지 않는 소셜 로그인 제공자입니다")
			);
		}
	}
}