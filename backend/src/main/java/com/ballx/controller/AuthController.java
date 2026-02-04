package com.ballx.controller;

import java.time.Duration;

import com.ballx.config.properties.JwtProperties;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballx.common.api.ApiSuccessResponse;
import com.ballx.domain.dto.request.AuthRequest;
import com.ballx.domain.dto.response.AuthResponse;
import com.ballx.service.auth.AuthService;
import com.ballx.service.auth.dto.TokenPair;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/api/v1/auth"))
public class AuthController {

	private final AuthService authService;
	private final JwtProperties jwtProperties;


	@PostMapping("/login")
	public ResponseEntity<ApiSuccessResponse<AuthResponse.LoginResponse>> login(
		@RequestBody @Valid AuthRequest.LoginRequest request,
		@RequestHeader(value = "X-Device-Id", required = false) String deviceId,
		HttpServletResponse response
	) {
		TokenPair tokens = authService.login(request.mobile(), deviceId);

		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", tokens.refreshToken())
			.httpOnly(true)
			.secure(false)
			.path("/")
			.sameSite("Strict")
			.maxAge(jwtProperties.refreshValidTime())
			.build();

		response.setHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

		return ApiSuccessResponse.wrap(AuthResponse.LoginResponse.of(tokens.accessToken()));
	}
}
