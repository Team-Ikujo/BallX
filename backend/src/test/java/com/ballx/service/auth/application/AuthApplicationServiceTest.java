package com.ballx.service.auth.application;

import com.ballx.constants.ProviderType;

import com.ballx.constants.messages.ErrorCode;
import com.ballx.constants.redis.RedisKey;
import com.ballx.domain.dto.response.social.SocialStateResponse;

import com.ballx.exception.CustomException;
import com.ballx.infra.cache.RedisCache;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest
public class AuthApplicationServiceTest {

	@Autowired
	AuthApplicationService authApplicationService;

	@Autowired
	RedisCache redisCache;

	static final String KEY_SEPARATOR = ":";

	@Test
	@DisplayName("method : issueState()")
	void state_생성_성공() {
		ProviderType provider = ProviderType.NAVER;
		SocialStateResponse response = authApplicationService.issueState(provider);
		assertNotNull(response);
		String state = response.state();
		log.info("state :: {}", response.state());
		String keyParam = provider + KEY_SEPARATOR + state;
		boolean isExists = redisCache.get(RedisKey.OAUTH_STATE.getKey(keyParam), Boolean.class);
		assertTrue(isExists);
		log.info("isExists :: {}", isExists);
	}

	@Test
	@DisplayName("method : issueState()")
	void state_생성_실패_kakao() {
		ProviderType provider = ProviderType.KAKAO;
		assertThatThrownBy(
			() -> authApplicationService.issueState(provider)
		).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.SOCIAL_PROVIDER_NOT_SUPPORTED.getMessage());
	}

	@ParameterizedTest
	@NullAndEmptySource
	@DisplayName("method : login() (google, naver)")
	void 엑세스토큰_생성_실패_state_필수_소셜로그_state_null_또는_공백(String code) {
		ProviderType provider = ProviderType.NAVER; // (or GOOGLE)
		assertThatThrownBy(
			() -> authApplicationService.login(provider, code, null)
		).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.MISSING_PARAMETER.format("state"));
	}

	@Test
	@DisplayName("method : login() (google, naver)")
	void 엑세스토큰_생성_실패_state_필수_소셜로그_redis_state_미존재() {
		ProviderType provider = ProviderType.NAVER; // (or GOOGLE)
		String code = "testCode";
		String state = "NOT_EXISTS_STATE";
		assertThatThrownBy(
			() -> authApplicationService.login(provider, code, state)
		).isInstanceOf(CustomException.class)
			.hasMessageContaining(ErrorCode.INVALID_STATE.getMessage());
	}
}
