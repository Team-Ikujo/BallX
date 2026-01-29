package com.ballx.service.user;

import com.ballx.domain.entity.user.UserEntity;
import com.ballx.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public Optional<UserEntity> findUserById(UUID userId) {
		return Optional.empty();
	}

	@Override
	public Optional<UserEntity> findUserByMobile(String mobile) {
		return Optional.empty();
	}
}
