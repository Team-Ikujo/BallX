package com.ballx.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum UserRole {
	ADMIN("관리자"),
	MEMBER("회원");

	private final String description;
}
