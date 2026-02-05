package com.ballx.security;

import java.util.UUID;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import com.ballx.constants.UserRole;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(callSuper = true)
public class ExtendedUserDetails extends User {
	private final UUID id;
	private final String mobile;

	public ExtendedUserDetails(UUID id, String mobile, UserRole role) {
		super(id.toString(),
			"",
			AuthorityUtils.createAuthorityList("ROLE_" + role.name()));
		this.id = id;
		this.mobile = mobile;
	}

}
