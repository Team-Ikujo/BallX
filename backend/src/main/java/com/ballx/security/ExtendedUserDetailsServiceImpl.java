package com.ballx.security;

import com.ballx.domain.entity.user.UserEntity;
import com.ballx.service.user.UserService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class ExtendedUserDetailsServiceImpl implements ExtendedUserDetailsService {

	private final UserService userService;

	@Override
	public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
		return userService.findUserByMobile(mobile).map(this::convert).orElseThrow(
			() -> new UsernameNotFoundException(mobile + " 번호로 등록된 사용자 정보를 찾을 수 없습니다.")
		);
	}

	@Override
	public UserDetails loadUserById(String userId) throws UsernameNotFoundException {
		Optional<UserEntity> user = userService.findUserById(UUID.fromString(userId));
		return user.map(this::convert).orElseThrow(
			() -> new UsernameNotFoundException("사용자 정보를 찾을 수 없습니다.")
		);
	}

	private UserDetails convert(UserEntity user) {
		return new ExtendedUserDetails(
			user.getId(),
			user.getMobile(),
			user.getRole()
		);
	}
}
