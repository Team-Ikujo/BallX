package com.ballx.domain.entity.user;

import static lombok.AccessLevel.*;

import java.time.LocalDate;

import com.ballx.constants.Gender;
import com.ballx.constants.UserRole;
import com.ballx.constants.UserStatus;
import com.ballx.domain.entity.base.ModificationTimestampEntity;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Table(name = "\"user\"",
	uniqueConstraints = {
		@UniqueConstraint(
			columnNames = {"mobile"}
		)})
@Entity(name = "user")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = PROTECTED)
public class UserEntity extends ModificationTimestampEntity {

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String mobile;

	@Column(nullable = false)
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate birthDate;

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
		String name, String mobile, LocalDate birthDate, UserRole role, Gender gender
	) {
		this.name = name;
		this.mobile = mobile;
		this.birthDate = birthDate;
		this.role = role;
		this.gender = gender;
		this.status = UserStatus.ACTIVATED;
	}
}
