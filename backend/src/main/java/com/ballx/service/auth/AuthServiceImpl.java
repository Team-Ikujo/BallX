package com.ballx.service.auth;

import com.ballx.config.jwt.JwtTokenProvider;
import com.ballx.config.properties.JwtProperties;
import com.ballx.constants.UserStatus;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.domain.dto.response.AuthResponse;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.exception.CustomException;
import com.ballx.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final UserRepository userRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtProperties jwtProperties;
	private final RedisTemplate<String, String> redisTemplate;
	private static final String REFRESH_KEY_PREFIX = "refreshToken:";

	private IssuedTokens issueTokens (MemberEntity user) {
		validateIssuable(user);

		String accessToken = jwtTokenProvider.createAccess(user.getId(), user.getMobile(), user.getRole());
		String refreshToken = jwtTokenProvider.createRefresh(user.getId());

		redisTemplate.opsForValue().set(
			refreshKey(user.getId()),
			refreshToken,
			jwtProperties.refreshValidTime()
		);

		return new IssuedTokens(accessToken, refreshToken);
	}

	private record IssuedTokens(
		String accessToken,
		String refreshToken
	) {}

	private String refreshKey(UUID userId) {
		return REFRESH_KEY_PREFIX + userId;
	}

	private void validateIssuable(MemberEntity user) {
		if (user.getStatus() != UserStatus.ACTIVATED) {
			throw new CustomException(ErrorCode.AUTH_USER_INACTIVE);
		}
	}
}
