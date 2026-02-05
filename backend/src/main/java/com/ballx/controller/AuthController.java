package com.ballx.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ballx.common.api.ApiSuccessResponse;
import com.ballx.constants.ProviderType;
import com.ballx.domain.dto.request.social.SocialLoginRequest;
import com.ballx.domain.dto.response.social.SocialCallbackResponse;
import com.ballx.domain.dto.response.social.SocialTokenInfoResponse;
import com.ballx.service.social.SocialAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final SocialAuthService socialAuthService;

	@GetMapping("/auth/{provider}/callback")
	public ResponseEntity<ApiSuccessResponse<SocialCallbackResponse>> getSocialCode(
		@PathVariable String provider,
		@Valid @RequestParam String code
	) {
		ProviderType providerType = ProviderType.upperProviderType(provider);

		SocialCallbackResponse response = socialAuthService.getCode(providerType, code);

		return ApiSuccessResponse.wrap(response);
	}

	@PostMapping("/api/v1/auth/{provider}/login")

	public ResponseEntity<ApiSuccessResponse<SocialTokenInfoResponse>> getSocialAccessToken(
		@PathVariable String provider,
		@Valid @RequestBody SocialLoginRequest request
	) {
		ProviderType providerType = ProviderType.upperProviderType(provider);

		SocialTokenInfoResponse response = socialAuthService.getAccessToken(providerType, request.code());

		return ApiSuccessResponse.wrap(response);
	}
}