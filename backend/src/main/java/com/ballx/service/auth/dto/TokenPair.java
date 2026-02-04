package com.ballx.service.auth.dto;

public record TokenPair(
	String accessToken,
	String refreshToken
) {}