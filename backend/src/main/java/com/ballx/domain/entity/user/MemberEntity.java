package com.ballx.domain.entity.user;

import static lombok.AccessLevel.*;

import java.time.LocalDate;

import org.springframework.util.StringUtils;

import com.ballx.constants.Gender;
import com.ballx.constants.UserRole;
import com.ballx.validation.Preconditions;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity(name = "member")
@DiscriminatorValue("MEMBER")
@NoArgsConstructor(access = PROTECTED)
public class MemberEntity extends UserEntity {

	private MemberEntity(
		String name, String mobile, Gender gender, LocalDate birthDate
	) {
		super(name, mobile, birthDate, UserRole.MEMBER, gender);
	}

	public static MemberEntity create(
		final String name,
		final String mobile,
		final Gender gender,
		final LocalDate birthDate
	) {
		validate(name, mobile, gender, birthDate);
		return new MemberEntity(name, mobile, gender, birthDate);
	}

	private static void validate(String name, String mobile, Gender gender, LocalDate birthDate) {
		Preconditions.domainValidate(
			StringUtils.hasText(name), "회원 이름은 비어 있을 수 없습니다."
		);

		Preconditions.domainValidate(
			StringUtils.hasText(mobile), "회원 휴대전화 번호는 비어 있을 수 없습니다."
		);

		Preconditions.domainValidate(
			gender != null, "회원 성별은 비어 있을 수 없습니다."
		);

		Preconditions.domainValidate(
			birthDate != null, "회원 생년월일은 비어 있을 수 없습니다."
		);

		Preconditions.domainValidate(
			birthDate.isBefore(LocalDate.now()), "회원 생년월일은 과거 날짜여야 합니다."
		);
	}
}
