package com.ballx.domain.entity.user;

import com.ballx.constants.Gender;
import com.ballx.constants.MemberStatus;

import com.ballx.constants.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity(name = "member")
@DiscriminatorValue("MEMBER")
@NoArgsConstructor(access = PROTECTED)
public class MemberEntity extends UserEntity {

	@Column(nullable = false)
	private MemberStatus status;

	private MemberEntity(
		String name, String mobile, Gender gender
	) {
		super(name, mobile, UserRole.MEMBER, gender);
		this.status = MemberStatus.ENABLED;
	}

	public static MemberEntity create(
		final String name,
		final String mobile,
		final Gender gender
	) {
		return new MemberEntity(name, mobile, gender);
	}
}
