package com.ballx.repository;

import com.ballx.constants.Gender;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.repository.user.MemberRepository;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@DataJpaTest
public class MemberRepositoryTest {

	@Autowired
	MemberRepository memberRepository;

	@Test
	void 회원_저장_테스트() {
		MemberEntity member = MemberEntity.create(
			"테스트회원",
			"01012341234",
			Gender.MALE
		);
		memberRepository.save(member);
		assertNotNull(member.getId());
		assertNotNull(member.getCreatedAt());
		assertNotNull(member.getUpdatedAt());
		log.info("member id : {}", member.getId());
	}

}
