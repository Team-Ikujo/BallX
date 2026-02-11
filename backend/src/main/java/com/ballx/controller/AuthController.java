package com.ballx.controller;

import com.ballx.domain.dto.request.social.SocialLoginRequest;

import com.ballx.domain.dto.response.social.SocialStateResponse;
import com.ballx.service.auth.application.AuthApplicationService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballx.common.api.ApiSuccessResponse;
import com.ballx.constants.ProviderType;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.ballx.common.api.ApiSuccessResponse.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthApplicationService authApplicationService;

	@GetMapping("/{provider}/state")
	public ResponseEntity<ApiSuccessResponse<SocialStateResponse>> getState(
		@PathVariable ProviderType provider
	) {
		return wrap(authApplicationService.issueState(provider));
	}

	// todo: String -> Pair<String, String> 값으로 변경 (response)
	@GetMapping("/{provider}/login")
	public ResponseEntity<ApiSuccessResponse<String>> getSocialCode(
		@PathVariable ProviderType provider,
		@RequestBody @Valid SocialLoginRequest request
	) {

		return wrap(
			authApplicationService.login(provider, request.code(), request.state())
		);
	}
}