package com.ballx.service.auth;

import java.util.UUID;

import jakarta.transaction.Transactional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.ballx.config.jwt.JwtTokenProvider;
import com.ballx.config.properties.JwtProperties;
import com.ballx.constants.UserStatus;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.exception.CustomException;
import com.ballx.repository.user.MemberRepository;
import com.ballx.service.auth.dto.TokenPair;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
	private final MemberRepository memberRepository;
	private final JwtTokenProvider jwtTokenProvider;
	private final JwtProperties jwtProperties;
	private final RedisTemplate<String, String> redisTemplate;
	private static final String REFRESH_KEY_PREFIX = "refreshToken:";

	@Override
	public TokenPair login(String mobile, String deviceId) {
		validateDeviceId(deviceId);

		MemberEntity member = memberRepository.findByMobile(mobile)
			.orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

		return issueTokens(member, deviceId);
	}

	private TokenPair issueTokens (MemberEntity member, String deviceId) {
		validateIssuable(member);

		String accessToken = jwtTokenProvider.createAccess(member.getId(), member.getMobile(), member.getRole());
		String refreshToken = jwtTokenProvider.createRefresh(member.getId());

		redisTemplate.opsForValue().set(
			refreshKey(member.getId(), deviceId),
			refreshToken,
			jwtProperties.refreshValidTime()
		);

		return new TokenPair(accessToken, refreshToken);
	}

	private String refreshKey(UUID userId, String deviceId) {
		return REFRESH_KEY_PREFIX + userId + ":" + deviceId;
	}

	private void validateIssuable(MemberEntity user) {
		if (user.getStatus() != UserStatus.ACTIVATED) {
			throw new CustomException(ErrorCode.AUTH_USER_INACTIVE);
		}
	}

	private void validateDeviceId(String deviceId) {
		if (deviceId == null || deviceId.isBlank()) {
			throw new CustomException(ErrorCode.AUTH_INVALID_DEVICE_ID);
		}
	}
}
