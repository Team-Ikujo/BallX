package com.ballx.controller.oauth;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ballx.common.api.ApiSuccessResponse;
import com.ballx.constants.AuthStatus;
import com.ballx.domain.dto.request.oauth.OAuth2LinkRequest;
import com.ballx.domain.dto.request.oauth.OAuth2SignUpRequest;
import com.ballx.domain.dto.response.oauth.OAuth2AccountResponse;
import com.ballx.domain.dto.response.oauth.OAuth2InfoResponse;
import com.ballx.domain.dto.response.oauth.OAuth2LinkResponse;
import com.ballx.domain.dto.response.oauth.OAuth2StatusResponse;
import com.ballx.domain.dto.response.oauth.OAuth2TokenResponse;
import com.ballx.service.AuthenticationService;
import com.ballx.service.oauth.OAuth2CookieService;
import com.ballx.service.oauth.OAuth2Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

	private final OAuth2Service oAuth2Service;
	private final OAuth2CookieService oAuth2CookieService;
	private final AuthenticationService authenticationService;

	// OAuth2 로그인 성공 후 콜백 처리
	@GetMapping("/callback")
	public ResponseEntity<ApiSuccessResponse<OAuth2StatusResponse>> handleOAuth2Callback(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		// OAuth2 정보 추출
		OAuth2InfoResponse oAuth2Info = oAuth2CookieService.getOAuth2InfoFromCookie(request);

		// 인증 상태 확인
		OAuth2StatusResponse statusResponse = oAuth2Service.checkOAuth2Status(
			oAuth2Info.provider(),
			oAuth2Info.providerId(),
			oAuth2Info.email()
		);

		// 로그인 성공 시 토큰 발급 및 쿠키 정리
		if (statusResponse.status() == AuthStatus.LOGIN_SUCCESS) {
			OAuth2TokenResponse authResponse = oAuth2Service.login(
				oAuth2Info.provider(),
				oAuth2Info.providerId()
			);

			response.setHeader("Authorization", "Bearer " + authResponse.accessToken());
			oAuth2CookieService.deleteOAuth2InfoCookie(request, response);
		}

		return ApiSuccessResponse.wrap(statusResponse);
	}

	// 회원가입
	@PostMapping("/signup")
	public ResponseEntity<ApiSuccessResponse<OAuth2TokenResponse>> signUp(
		@Valid @RequestBody OAuth2SignUpRequest request,
		HttpServletRequest httpRequest,
		HttpServletResponse httpResponse
	) {
		// OAuth2 정보 추출
		OAuth2InfoResponse oAuth2Info = oAuth2CookieService.getOAuth2InfoFromCookie(httpRequest);

		// 회원가입 처리
		OAuth2TokenResponse response = oAuth2Service.signUp(
			request,
			oAuth2Info.provider(),
			oAuth2Info.providerId(),
			oAuth2Info.email()
		);

		// 쿠키 정리
		oAuth2CookieService.deleteOAuth2InfoCookie(httpRequest, httpResponse);

		return ApiSuccessResponse.wrap(response);
	}

	// 소셜 계정 연동
	@PostMapping("/link-account")
	public ResponseEntity<ApiSuccessResponse<OAuth2LinkResponse>> linkAccount(
		@Valid @RequestBody OAuth2LinkRequest request,
		HttpServletRequest httpRequest,
		HttpServletResponse httpResponse
	) {
		// OAuth2 정보 추출
		OAuth2InfoResponse oAuth2Info = oAuth2CookieService.getOAuth2InfoFromCookie(httpRequest);

		// 계정 연동 처리
		OAuth2LinkResponse response = oAuth2Service.linkAccount(
			request,
			oAuth2Info.provider(),
			oAuth2Info.providerId(),
			oAuth2Info.email()
		);

		// 쿠키 정리
		oAuth2CookieService.deleteOAuth2InfoCookie(httpRequest, httpResponse);

		return ApiSuccessResponse.wrap(response);
	}

	// OAuth2 연동 해제 콜백
	@GetMapping("/unlink")
	public ResponseEntity<ApiSuccessResponse<Map<String, String>>> handleOAuth2Unlink(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) {
		// 사용자 인증 정보 추출
		UUID memberId = authenticationService.getMemberId(authentication);

		// OAuth2 정보 추출
		OAuth2InfoResponse oAuth2Info = oAuth2CookieService.getOAuth2InfoFromCookie(request);

		// 연동 해제 처리
		oAuth2Service.unlinkOAuth2Account(
			memberId,
			oAuth2Info.provider(),
			oAuth2Info.providerId(),
			oAuth2Info.accessToken()
		);

		// 쿠키 정리
		oAuth2CookieService.deleteOAuth2InfoCookie(request, response);

		return ApiSuccessResponse.wrap(Map.of(
			"message", "소셜 계정 연동이 해제되었습니다.",
			"provider", oAuth2Info.provider().name()
		));
	}

	// 연동된 소셜 계정 목록 조회
	@GetMapping("/linked-accounts")
	public ResponseEntity<ApiSuccessResponse<List<OAuth2AccountResponse>>> getLinkedAccounts(
		Authentication authentication
	) {
		UUID memberId = authenticationService.getMemberId(authentication);
		List<OAuth2AccountResponse> linkedAccounts =
			oAuth2Service.getLinkedOAuth2Accounts(memberId);

		return ApiSuccessResponse.wrap(linkedAccounts);
	}
}
