package com.ballx.service.auth;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import com.ballx.config.jwt.JwtTokenProvider;
import com.ballx.config.properties.JwtProperties;
import com.ballx.constants.Gender;
import com.ballx.constants.UserStatus;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.exception.CustomException;
import com.ballx.repository.user.MemberRepository;
import com.ballx.service.auth.dto.TokenPair;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

	@InjectMocks
	private AuthServiceImpl authService;
	@Mock
	private MemberRepository memberRepository;
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
	void 로그인시_활성회원이면_토큰발급되고_기기별_리프레시가_레디스에_저장된다() {
		// given
		String mobile = "01012345678";
		String deviceId = "device-001";
		MemberEntity member = activeMember();

		given(memberRepository.findByMobile(mobile))
			.willReturn(Optional.of(member));

		given(jwtTokenProvider.createAccess(any(), any(), any()))
			.willReturn("access-token");

		given(jwtTokenProvider.createRefresh(any()))
			.willReturn("refresh-token");

		given(jwtProperties.refreshValidTime())
			.willReturn(Duration.ofDays(7));

		given(redisTemplate.opsForValue())
			.willReturn(valueOperations);

		// when
		TokenPair result = authService.login(mobile, deviceId);

		// then
		assertThat(result.accessToken()).isEqualTo("access-token");
		assertThat(result.refreshToken()).isEqualTo("refresh-token");

		verify(jwtTokenProvider).createAccess(member.getId(), member.getMobile(), member.getRole());
		verify(jwtTokenProvider).createRefresh(member.getId());

		verify(valueOperations).set(
			"refreshToken:" + member.getId() + ":" + deviceId,
			"refresh-token",
			Duration.ofDays(7)
		);
	}

	@Test
	void 디바이스아이디가_비어있으면_예외가_발생한다() {
		// given
		String mobile = "01012345678";
		String deviceId = "   ";

		// when / then
		assertThatThrownBy(() -> authService.login(mobile, deviceId))
			.isInstanceOf(CustomException.class)
			.satisfies(ex -> {
				CustomException ce = (CustomException) ex;
				assertThat(ce.error()).isEqualTo(ErrorCode.AUTH_INVALID_DEVICE_ID);
			});

		then(memberRepository).shouldHaveNoInteractions();
	}

	@Test
	void 비활성_회원이면_예외가_발생한다() {
		// given
		String mobile = "01012345678";
		String deviceId = "device-001";
		MemberEntity member = activeMember();

		ReflectionTestUtils.setField(member, "status", UserStatus.SUSPENDED);

		given(memberRepository.findByMobile(mobile))
			.willReturn(Optional.of(member));

		// when / then
		assertThatThrownBy(() -> authService.login(mobile, deviceId))
			.isInstanceOf(CustomException.class)
			.satisfies(ex -> {
				CustomException ce = (CustomException) ex;
				assertThat(ce.error()).isEqualTo(ErrorCode.AUTH_USER_INACTIVE);
			});

		then(jwtTokenProvider).shouldHaveNoInteractions();
		then(redisTemplate).shouldHaveNoInteractions();
	}
}