package com.ballx.service.auth.application;

import com.ballx.constants.ProviderType;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.constants.redis.RedisKey;
import com.ballx.domain.dto.response.social.SocialStateResponse;
import com.ballx.exception.CustomException;
import com.ballx.infra.cache.RedisCache;
import com.ballx.infra.oauth.SocialApiClient;
import com.ballx.infra.oauth.SocialClientProvider;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService {

	private final SocialClientProvider socialClientProvider;
	private final RedisCache redisCache;

	static final String KEY_SEPARATOR = ":";

	// todo : return 값 jwt Pair<String, String> 로 변경
	// todo : accessToken -> providerId 값 요청 로직 추가
	public String login(ProviderType provider, String code, String state) {
		validateState(provider, state);
		String accessToken = getAccessToken(provider, code, state);
		return accessToken;
	}

	public SocialStateResponse issueState(ProviderType provider) {
		if (provider == ProviderType.KAKAO) {
			throw new CustomException(ErrorCode.SOCIAL_PROVIDER_NOT_SUPPORTED);
		}
		String state = UUID.randomUUID().toString();
		String keyParam = provider + KEY_SEPARATOR + state;

		redisCache.set(RedisKey.OAUTH_STATE, keyParam, true);

		return SocialStateResponse.of(state);
	}

	private void validateState(ProviderType provider, String state) {
		if (provider == ProviderType.KAKAO)
			return;

		if (state == null || state.isBlank()) {
			throw new CustomException(ErrorCode.MISSING_PARAMETER, "state");
		}
		String keyParam = provider + KEY_SEPARATOR + state;
		log.info("keyParam : {}", keyParam);
		String stateKey = RedisKey.OAUTH_STATE.getKey(keyParam);
		if (!redisCache.consume(stateKey)) {
			throw new CustomException(ErrorCode.INVALID_STATE);
		}
	}

	private String getAccessToken(ProviderType provider, String code, String state) {
		SocialApiClient socialApiClient = socialClientProvider.getClient(provider);
		return socialApiClient.getAccessToken(code, state);
	}

}
