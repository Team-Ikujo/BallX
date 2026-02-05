package com.ballx.repository.social;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ballx.constants.Gender;
import com.ballx.constants.ProviderType;
import com.ballx.domain.entity.social.SocialProviderEntity;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.repository.user.MemberRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DataJpaTest
class SocialRepositoryTest {
	@Autowired
	SocialRepository socialRepository;

	@Autowired
	MemberRepository memberRepository;

	@Test
	void 소셜_저장_테스트() {

		MemberEntity member = MemberEntity.create(
			"홍길동",
			"010-1234-5678",
			Gender.MALE,
			LocalDate.of(2026, 2, 4)
		);
		MemberEntity savedMember = memberRepository.save(member);

		SocialProviderEntity provider = SocialProviderEntity.create(
			savedMember,
			ProviderType.GOOGLE,
			"01012341234",
			"google@gmail.com"
		);
		socialRepository.save(provider);
		assertNotNull(provider.getMember());
		assertNotNull(provider.getProvider());
		assertNotNull(provider.getProviderId());
		assertNotNull(provider.getEmail());
		log.info("provider id : {}", provider.getId());
	}

}