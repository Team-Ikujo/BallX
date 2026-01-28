package com.ballx.domain.entity.user;

import com.ballx.constants.Gender;

import com.ballx.exception.FieldValidationException;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
@ActiveProfiles("test")
public class MemberEntityTest {

	static final String NAME = "테스트회원";
	static final String MOBILE = "01012345678";
	MemberEntity member;

	@Test
	void 회원_도메인_생성_성공() {
		member = MemberEntity.create(
			"테스트회원",
			"01012341234",
			Gender.MALE
		);
		log.info("member name : {}", member.getName());
		log.info("member role : {}", member.getRole());
		assertNotNull(member);

	}

	@ParameterizedTest
	@NullAndEmptySource
	void 회원_도메인_생성_실패_이름_null_또는_공백(String name) {
		assertThatThrownBy(
			() -> MemberEntity.create(name, MOBILE, Gender.MALE)
		).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("회원 이름은 비어 있을 수 없습니다.");
	}

	@ParameterizedTest
	@NullAndEmptySource
	void 회원_도메인_생성_실패_휴대전화_null_또는_공백(String mobile) {
		assertThatThrownBy(
			() -> MemberEntity.create("테스트회원", mobile, Gender.MALE)
		).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("회원 휴대전화 번호는 비어 있을 수 없습니다.");
	}

	@Test
	void 회원_도메인_생성_실패_성별_null() {
		assertThatThrownBy(
			() -> MemberEntity.create(NAME, MOBILE, null)
		).isInstanceOf(FieldValidationException.class)
			.hasMessageContaining("회원 성별은 비어 있을 수 없습니다.");
	}
}
