package com.ballx.domain.entity.user;

import com.ballx.constants.Gender;

import com.ballx.constants.UserRole;

import com.ballx.validation.Preconditions;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.util.StringUtils;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "member")
@DiscriminatorValue("MEMBER")
@NoArgsConstructor(access = PROTECTED)
public class MemberEntity extends UserEntity {

	private MemberEntity(
		String name, String mobile, Gender gender
	) {
		super(name, mobile, UserRole.MEMBER, gender);
	}

	public static MemberEntity create(
		final String name,
		final String mobile,
		final Gender gender
	) {
		validate(name, mobile, gender);
		return new MemberEntity(name, mobile, gender);
	}

	private static void validate(String name, String mobile, Gender gender) {
		Preconditions.domainValidate(
			StringUtils.hasText(name), "회원 이름은 비어 있을 수 없습니다."
		);

		Preconditions.domainValidate(
			StringUtils.hasText(mobile), "회원 휴대전화 번호는 비어 있을 수 없습니다."
		);

		Preconditions.domainValidate(
			gender != null, "회원 성별은 비어 있을 수 없습니다."
		);
	}
}
