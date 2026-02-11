package com.ballx.service.auth;

import com.ballx.constants.redis.RedisKey;
import com.ballx.infra.cache.RedisCache;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class AuthServiceTest {

	@Autowired
	AuthService authService;

	@Autowired
	RedisCache redisCache;

	final static String MOBILE = "01043053451";

	@Test
	@Disabled("비용 발생으로 테스트 시 Disabled 주석 처리 후 테스트 진행")
	void 휴대폰_인증번호_전송_redis_저장() {
		authService.sendAuthCode(MOBILE);
		String authCode = redisCache.get(RedisKey.AUTH_CODE.getKey(MOBILE), String.class);
		assertNotNull(authCode);
		log.info("authCode :: {}", authCode);
	}

}
