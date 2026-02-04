package com.ballx.constants.redis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;

@Getter
@RequiredArgsConstructor
public enum RedisKey {
	AUTH_CODE("auth:code:", Duration.ofMinutes(3));

	private final String prefix;

	private final Duration ttl;

	public String getKey(Object val) {
		return prefix + val;
	}
}
