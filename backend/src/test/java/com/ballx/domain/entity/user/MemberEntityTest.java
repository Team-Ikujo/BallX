package com.ballx.domain.entity.user;

import com.ballx.constants.Gender;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@ActiveProfiles("test")
public class MemberEntityTest {

	@Test
	void 회원_도메인_생성() {
		MemberEntity member = MemberEntity.create(
			"테스트회원",
			"01012341234",
			Gender.MALE
		);
		log.info("member name : {}", member.getName());
		log.info("member role : {}", member.getRole());
		assertNotNull(member);

	}
}
