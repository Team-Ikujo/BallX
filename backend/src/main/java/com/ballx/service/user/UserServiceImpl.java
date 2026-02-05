package com.ballx.service.user;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ballx.domain.entity.user.UserEntity;
import com.ballx.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	public Optional<UserEntity> findUserById(UUID userId) {
		return userRepository.findById(userId);
	}

	@Override
	public Optional<UserEntity> findUserByMobile(String mobile) {
		return userRepository.findByMobile(mobile);
	}
}
