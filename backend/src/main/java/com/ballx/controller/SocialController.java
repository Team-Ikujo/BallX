package com.ballx.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballx.constants.ProviderType;
import com.ballx.domain.dto.request.social.SocialLoginRequest;
import com.ballx.domain.dto.response.social.SocialTokenInfoResponse;
import com.ballx.service.social.SocialAuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class SocialController {

	private final SocialAuthService socialAuthService;

	@PostMapping("/{provider}/login")
	public ResponseEntity<SocialTokenInfoResponse> getSocialAccessToken(
		@PathVariable("provider") ProviderType provider,
		@Valid @RequestBody SocialLoginRequest request
	) {
		SocialTokenInfoResponse response = socialAuthService.getAccessToken(provider, request.code());
		return ResponseEntity.ok(response);
	}
}