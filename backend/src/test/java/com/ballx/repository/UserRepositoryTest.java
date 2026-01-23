package com.ballx.repository;

import com.ballx.constants.Gender;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.domain.entity.user.UserEntity;
import com.ballx.repository.user.MemberRepository;
import com.ballx.repository.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
public class UserRepositoryTest {

	@Autowired
	UserRepository userRepository;

	@Autowired
	MemberRepository memberRepository;

	MemberEntity member;

	@BeforeEach
	void setup() {
		member = MemberEntity.create(
			"테스트회원",
			"01012341234",
			Gender.MALE
		);
		memberRepository.save(member);
	}

	@Test
	void 유저_저장_테스트() {
		assertNotNull(member.getId());
		UserEntity user = userRepository.findById(member.getId()).orElse(null);
		assertNotNull(user);
		assertNotNull(user.getId());
		assertEquals(member.getId(), user.getId());
		log.info("user id : {}", user.getId());
		log.info("member id : {}", member.getId());
		log.info("user role : {}", user.getRole());

	}
}
