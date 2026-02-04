package com.ballx.service.auth;

import com.ballx.constants.redis.RedisKey;
import com.ballx.infra.cache.RedisCache;
import com.ballx.infra.sms.SmsProvider;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final SmsProvider smsProvider;
	private final RedisCache redisCache;

	@Override
	public void sendAuthCode(String mobile) {
		String authCode = getAuthenticationCode();
		smsProvider.send(mobile, authCode);
		redisCache.set(RedisKey.AUTH_CODE, mobile, authCode);
	}

	private String getAuthenticationCode() {
		int code = new Random().nextInt(900000) + 100000;
		return String.valueOf(code);
	}
}
