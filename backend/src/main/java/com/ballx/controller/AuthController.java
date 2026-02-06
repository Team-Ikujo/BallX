package com.ballx.controller;

import com.ballx.common.api.ApiSuccessResponse;
import com.ballx.domain.dto.request.AuthCodeRequest;
import com.ballx.service.auth.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ballx.common.api.ApiSuccessResponse.empty;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/mobile/code")
	public ResponseEntity<ApiSuccessResponse<Void>> sendAuthCode(
		@RequestBody @Valid AuthCodeRequest request
	) {
		authService.sendAuthCode(request.mobile());
		return empty();
	}
}
