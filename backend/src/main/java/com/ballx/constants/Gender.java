package com.ballx.constants;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {
	MALE("남"),
	FEMALE("여");

	private final String description;
}
