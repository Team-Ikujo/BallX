package com.ballx.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ballx.constants.Gender;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.repository.user.MemberRepository;

import lombok.extern.slf4j.Slf4j;

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
			Gender.MALE,
			LocalDate.of(2026, 2, 4)
		);
		memberRepository.save(member);
		assertNotNull(member.getId());
		assertNotNull(member.getCreatedAt());
		assertNotNull(member.getUpdatedAt());
		log.info("member id : {}", member.getId());
	}

}
