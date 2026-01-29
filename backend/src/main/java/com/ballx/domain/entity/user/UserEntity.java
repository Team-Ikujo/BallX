package com.ballx.domain.entity.user;

import com.ballx.constants.Gender;
import com.ballx.constants.UserRole;
import com.ballx.constants.UserStatus;
import com.ballx.domain.entity.base.ModificationTimestampEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Table(name = "\"user\"")
@Entity(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = PROTECTED)
public class UserEntity extends ModificationTimestampEntity {

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String mobile;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserRole role;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private UserStatus status;

	protected UserEntity(
		String name, String mobile, UserRole role, Gender gender
	) {
		this.name = name;
		this.mobile = mobile;
		this.role = role;
		this.gender = gender;
		this.status = UserStatus.ACTIVATED;
	}
}
