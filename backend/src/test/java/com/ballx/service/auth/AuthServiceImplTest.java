package com.ballx.service.auth;

import com.ballx.config.jwt.JwtTokenProvider;
import com.ballx.config.properties.JwtProperties;
import com.ballx.constants.Gender;
import com.ballx.constants.UserStatus;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.exception.CustomException;
import com.ballx.repository.user.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@InjectMocks
	private AuthServiceImpl authService;
	@Mock
	private JwtTokenProvider jwtTokenProvider;
	@Mock
	private JwtProperties jwtProperties;
	@Mock
	private RedisTemplate<String, String> redisTemplate;
	@Mock
	private ValueOperations<String, String> valueOperations;

	private MemberEntity activeMember() {
		return MemberEntity.create(
			"홍길동",
			"01012345678",
			Gender.MALE
		);
	}

	@Test
	void 활성화된_회원이면_토큰이_발급되고_리프레시가_레디스에_저장된다() {
		// given
		MemberEntity member = activeMember();

		given(jwtTokenProvider.createAccess(any(), any(), any()))
			.willReturn("access-token");

		given(jwtTokenProvider.createRefresh(any()))
			.willReturn("refresh-token");

		given(jwtProperties.refreshValidTime())
			.willReturn(Duration.ofDays(7));

		given(redisTemplate.opsForValue())
			.willReturn(valueOperations);

		// when
		var result = invokeIssueTokens(member);

		// then
		String accessToken = (String) ReflectionTestUtils.invokeMethod(result, "accessToken");
		String refreshToken = (String) ReflectionTestUtils.invokeMethod(result, "refreshToken");

		assertThat(accessToken).isEqualTo("access-token");
		assertThat(refreshToken).isEqualTo("refresh-token");

		verify(jwtTokenProvider).createAccess(
			member.getId(),
			member.getMobile(),
			member.getRole()
		);

		verify(jwtTokenProvider).createRefresh(member.getId());

		verify(valueOperations).set(
			"refreshToken:" + member.getId(),
			"refresh-token",
			Duration.ofDays(7)
		);
	}

	private Object invokeIssueTokens(MemberEntity member) {
		return ReflectionTestUtils.invokeMethod(authService, "issueTokens", member);
	}
}