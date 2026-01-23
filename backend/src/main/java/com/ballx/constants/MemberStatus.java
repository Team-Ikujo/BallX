package com.ballx.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MemberStatus {

	ENABLED("정상"),
	DISABLED("비정상");

	private final String description;

}
