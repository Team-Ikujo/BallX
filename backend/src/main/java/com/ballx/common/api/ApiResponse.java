package com.ballx.common.api;

import lombok.Getter;

import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class ApiResponse {
	private final String code;
	private final String message;
}