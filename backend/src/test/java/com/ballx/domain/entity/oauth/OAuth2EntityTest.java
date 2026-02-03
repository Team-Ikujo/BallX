package com.ballx.domain.entity.oauth;

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import com.ballx.constants.Gender;
import com.ballx.constants.OAuth2Provider;
import com.ballx.domain.entity.user.MemberEntity;
import com.ballx.exception.FieldValidationException;
import com.ballx.repository.oauth.OAuth2Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("test")
@DataJpaTest
class OAuth2EntityTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private OAuth2Repository oAuth2Repository;

	private MemberEntity member;

	@BeforeEach
	void setUp() {
		member = MemberEntity.create(
			"홍길동",
			"010-1234-5678",
			Gender.MALE
		);
		entityManager.persist(member);
		entityManager.flush();

		log.info(" Member 저장됨:");
		log.info(" - MemberId: {}", member.getId());
		log.info(" - Name: {}", member.getName());
		log.info(" - Mobile: {}", member.getMobile());
		log.info(" - Gender: {}", member.getGender());
	}

	@Test
	void 생성_성공() {
		OAuth2Entity oAuth2Entity = OAuth2Entity.create(
			member,
			OAuth2Provider.GOOGLE,
			"google-12345",
			"google@gmail.com"
		);

		OAuth2Entity savedOAuth2 = entityManager.persistAndFlush(oAuth2Entity);

		assertThat(savedOAuth2.getId()).isNotNull();
		assertThat(savedOAuth2.getProvider()).isEqualTo(OAuth2Provider.GOOGLE);
		assertThat(savedOAuth2.getProviderId()).isEqualTo("google-12345");
		assertThat(savedOAuth2.getEmail()).isEqualTo("google@gmail.com");
		assertThat(savedOAuth2.getCreatedAt()).isNotNull();
		assertThat(savedOAuth2.getUpdatedAt()).isNotNull();

		log.info(" OAuth2Entity 저장됨:");
		log.info(" - ID: {}", savedOAuth2.getId());
		log.info(" - Provider: {}", savedOAuth2.getProvider());
		log.info(" - ProviderId: {}", savedOAuth2.getProviderId());
		log.info(" - Email: {}", savedOAuth2.getEmail());
		log.info(" - Member: {}", savedOAuth2.getMember().getId());
		log.info(" - CreatedAt: {}", savedOAuth2.getCreatedAt());
		log.info(" - UpdatedAt: {}", savedOAuth2.getUpdatedAt());
	}

	@Test
	void 생성_실패_멤버가_없거나_null() {
		assertThatThrownBy(() -> {
			OAuth2Entity.create(
				null,
				OAuth2Provider.GOOGLE,
				"google-12345",
				"google@gmail.com"
			);
		})
			.isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("멤버는 비어 있을 수 없습니다.")
			.satisfies(e1 -> {
				log.info("예외 발생 확인: {}", e1.getMessage());
			});
	}

	@Test
	void 생성_실패_제공자가_없거나_null() {
		assertThatThrownBy(() -> {
			OAuth2Entity.create(
				member,
				null,
				"google-12345",
				"google@gmail.com"
			);
		}).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("제공자는 비어 있을 수 없습니다.")
			.satisfies(e2 -> {
				log.info("예외 발생 확인: {}", e2.getMessage());
			});
	}

	@Test
	void 생성_실패_제공자번호가_없거나_null() {
		assertThatThrownBy(() -> {
			OAuth2Entity.create(
				member,
				OAuth2Provider.GOOGLE,
				null,
				"google@gmail.com"
			);
		}).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("제공자 번호는 비어 있을 수 없습니다.")
			.satisfies(e3 -> {
				log.info("예외 발생 확인 : {}", e3.getMessage());
			});
	}

	@Test
	void 생성_실패_이메일이_없거나_null() {
		assertThatThrownBy(() -> {
			OAuth2Entity.create(
				member,
				OAuth2Provider.GOOGLE,
				"google-12345",
				null
			);
		}).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("이메일은 비어 있을 수 없습니다.")
			.satisfies(e4 -> {
				log.info("예외 발생 확인 : {}", e4.getMessage());
			});
	}

	@Test
	void 관계_확인_성공() {
		OAuth2Entity oauth2 = OAuth2Entity.create(
			member,
			OAuth2Provider.KAKAO,
			"kakao-12345",
			"kakao@kakao.com"
		);

		entityManager.persistAndFlush(oauth2);
		UUID savedId = oauth2.getId();
		entityManager.clear();

		OAuth2Entity foundOAuth2 = entityManager.find(OAuth2Entity.class, savedId);

		assertThat(foundOAuth2).isNotNull();
		assertThat(foundOAuth2.getMember()).isNotNull();
		assertThat(foundOAuth2.getMember().getId()).isEqualTo(member.getId());
		assertThat(foundOAuth2.getMember().getName()).isEqualTo("홍길동");

		log.info(" 조회 성공:");
		log.info(" - OAuth2 ID: {}", foundOAuth2.getId());
		log.info(" - Member ID: {}", foundOAuth2.getMember().getId());
		log.info(" - Member Name: {}", foundOAuth2.getMember().getName());
	}

	@Test
	void 관계_확인_다른_멤버() {
		MemberEntity member2 = MemberEntity.create(
			"홍길동",
			"010-1234-5678",
			Gender.FEMALE
		);
		entityManager.persistAndFlush(member2);

		OAuth2Entity oauth2 = OAuth2Entity.create(
			member,
			OAuth2Provider.KAKAO,
			"kakao-12345",
			"kakao@kakao.com"
		);

		entityManager.persistAndFlush(oauth2);
		UUID savedId = oauth2.getId();

		entityManager.clear();

		OAuth2Entity foundOAuth2 = entityManager.find(OAuth2Entity.class, savedId);

		assertThat(foundOAuth2).isNotNull();
		assertThat(foundOAuth2.getMember()).isNotNull();
		assertThat(foundOAuth2.getMember().getId())
			.isNotEqualTo(member2.getId())
			.isEqualTo(member.getId());
		log.info(" - OAuth2 ID: {}", foundOAuth2.getId());
		log.info(" - Member ID: {}", foundOAuth2.getMember().getId());
		log.info(" - Member2 ID: {}", member2.getId());
	}

	@Test
	void 다중_제공자_연결() {
		OAuth2Entity googleOAuth = OAuth2Entity.create(
			member,
			OAuth2Provider.GOOGLE,
			"google-12345",
			"google@gmail.com"
		);

		OAuth2Entity kakaoOAuth = OAuth2Entity.create(
			member,
			OAuth2Provider.KAKAO,
			"kakao-12345",
			"kakao@kakao.com"
		);

		OAuth2Entity naverOAuth = OAuth2Entity.create(
			member,
			OAuth2Provider.NAVER,
			"naver-12345",
			"naver@naver.com"
		);

		entityManager.persist(googleOAuth);
		entityManager.persist(kakaoOAuth);
		entityManager.persist(naverOAuth);
		entityManager.flush();

		log.info(" - google ID: {}", googleOAuth.getId());
		log.info(" - kakao ID: {}", kakaoOAuth.getId());
		log.info(" - naver ID: {}", naverOAuth.getId());

		entityManager.clear();
		OAuth2Entity foundGoogle = entityManager.find(OAuth2Entity.class, googleOAuth.getId());
		OAuth2Entity foundKakao = entityManager.find(OAuth2Entity.class, kakaoOAuth.getId());
		OAuth2Entity foundNaver = entityManager.find(OAuth2Entity.class, naverOAuth.getId());

		assertThat(foundGoogle.getProvider()).isEqualTo(OAuth2Provider.GOOGLE);
		assertThat(foundGoogle.getEmail()).isEqualTo("google@gmail.com");

		assertThat(foundKakao.getProvider()).isEqualTo(OAuth2Provider.KAKAO);
		assertThat(foundKakao.getEmail()).isEqualTo("kakao@kakao.com");

		assertThat(foundNaver.getProvider()).isEqualTo(OAuth2Provider.NAVER);
		assertThat(foundNaver.getEmail()).isEqualTo("naver@naver.com");

		assertThat(foundGoogle.getMember().getId()).isEqualTo(member.getId());
		assertThat(foundKakao.getMember().getId()).isEqualTo(member.getId());
		assertThat(foundNaver.getMember().getId()).isEqualTo(member.getId());

		log.info(" - google: {}", googleOAuth.getMember().getId());
		log.info(" - kakao: {}", kakaoOAuth.getMember().getId());
		log.info(" - naver: {}", naverOAuth.getMember().getId());
		log.info(" - member: {}", member.getId());
	}

	@Test
	void 이미_연결한_제공자_연결_실패() {
		OAuth2Entity googleOAuth = OAuth2Entity.create(
			member,
			OAuth2Provider.GOOGLE,
			"google-12345",
			"google@gmail.com"
		);
		entityManager.persistAndFlush(googleOAuth);
		entityManager.clear();

		assertThatThrownBy(() -> {
			if (oAuth2Repository.existsByMemberAndProvider(member, OAuth2Provider.GOOGLE)) {
				throw new RuntimeException("이미 연동 되어 있는 제공자입니다.");
			}
			OAuth2Entity googleOAuth2 = OAuth2Entity.create(
				member,
				OAuth2Provider.GOOGLE,
				"google-98765",
				"gmail@gmail.com"
			);
			oAuth2Repository.saveAndFlush(googleOAuth2);
		})
			.isInstanceOf(RuntimeException.class)
			.hasMessageContaining("이미 연동 되어 있는 제공자입니다.")
			.satisfies(e5 -> {
				log.info("예외 발생 확인 : {}", e5.getMessage());
			});
	}

}
