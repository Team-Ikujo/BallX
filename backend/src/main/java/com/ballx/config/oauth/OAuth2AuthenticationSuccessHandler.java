package com.ballx.config.oauth;

import static com.ballx.config.oauth.HttpCookieOAuth2AuthorizationRequestRepository.*;

import java.io.IOException;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.ballx.config.properties.OAuth2Properties;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.domain.dto.response.oauth.OAuth2UserInfo;
import com.ballx.exception.CustomException;
import com.ballx.security.OAuth2UserPrincipal;
import com.ballx.utils.CookieUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

	private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;
	private final OAuth2Properties oAuth2Properties;

	@Override
	public void onAuthenticationSuccess(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) throws IOException {

		log.info("🎯 OAuth2 Authentication Success Triggered");
		log.info("   Request URI: {}", request.getRequestURI());
		log.info("   Query String: {}", request.getQueryString());

		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			log.warn("⚠️ Response already committed. Cannot redirect to {}", targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);

		log.info("🚀 Redirecting to: {}", targetUrl);
		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) {
		// 1. 쿠키에서 redirect_uri 추출
		Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
			.map(Cookie::getValue);

		log.debug("📍 Redirect URI from cookie: {}", redirectUri.orElse("(none)"));

		// 2. redirect_uri 검증
		if (redirectUri.isPresent()) {
			String uri = redirectUri.get();
			if (!oAuth2Properties.isAuthorizedRedirectUri(uri)) {
				log.error("🚨 Unauthorized Redirect URI: {}", uri);
				throw new CustomException(ErrorCode.UNAUTHORIZED_REDIRECT_URI);
			}
		}

		// 3. mode 추출 (login | unlink)
		String mode = CookieUtils.getCookie(request, MODE_PARAM_COOKIE_NAME)
			.map(Cookie::getValue)
			.orElse("login");

		log.debug("🔧 OAuth2 Mode: {}", mode);

		// 4. 기본 targetUrl 결정
		String targetUrl = redirectUri.orElse(
			"login".equalsIgnoreCase(mode)
				? oAuth2Properties.defaultRedirectUri()
				: "/api/auth/oauth2/unlink"
		);

		// 5. Principal 추출 및 검증
		OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);
		if (principal == null) {
			log.error("🚨 OAuth2UserPrincipal is null");
			return buildErrorUrl(targetUrl, "Authentication failed");
		}

		OAuth2UserInfo userInfo = principal.getUserInfo();

		// 6. Mode별 처리
		switch (mode.toLowerCase()) {
			case "login":
				return handleLoginMode(response, targetUrl, userInfo);
			case "unlink":
				return handleUnlinkMode(response, targetUrl, userInfo);
			default:
				log.warn("⚠️ Unknown mode: {}, defaulting to login", mode);
				return handleLoginMode(response, targetUrl, userInfo);
		}
	}

	// 로그인 모드 처리
	private String handleLoginMode(
		HttpServletResponse response,
		String targetUrl,
		OAuth2UserInfo userInfo
	) {
		saveOAuth2InfoToCookie(response, userInfo);

		log.info("✅ OAuth2 Login Success");
		log.info("   Provider: {}", userInfo.getProvider());
		log.info("   Email: {}", userInfo.getEmail());
		log.info("   Provider ID: {}", userInfo.getProviderId());

		// 프론트엔드로 리다이렉트 (provider 정보 포함)
		return UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam("provider", userInfo.getProvider().name())
			.build()
			.toUriString();
	}

	// 연동해제 모드 처리
	private String handleUnlinkMode(
		HttpServletResponse response,
		String targetUrl,
		OAuth2UserInfo userInfo
	) {
		saveOAuth2InfoToCookie(response, userInfo);

		log.info("🔗 OAuth2 Unlink Request");
		log.info("   Provider: {}", userInfo.getProvider());
		log.info("   Provider ID: {}", userInfo.getProviderId());
		log.info("   Email: {}", userInfo.getEmail());

		return targetUrl;
	}

	// OAuth2 정보를 쿠키에 저장 (5분 유효)
	private void saveOAuth2InfoToCookie(HttpServletResponse response, OAuth2UserInfo userInfo) {
		int maxAge = 300; // 5분

		CookieUtils.addCookie(response, "oauth2_provider",
			userInfo.getProvider().name(), maxAge);
		CookieUtils.addCookie(response, "oauth2_provider_id",
			userInfo.getProviderId(), maxAge);
		CookieUtils.addCookie(response, "oauth2_email",
			userInfo.getEmail(), maxAge);

		// AccessToken은 Secure Cookie로 저장 (HTTPS 환경에서만 전송)
		if (userInfo.getAccessToken() != null) {
			// 프로덕션 환경에서는 addSecureCookie 사용 권장
			// 로컬 개발(HTTP)에서는 addCookie 사용
			if (isSecureEnvironment()) {
				CookieUtils.addSecureCookie(response, "oauth2_access_token",
					userInfo.getAccessToken(), maxAge);
			} else {
				CookieUtils.addCookie(response, "oauth2_access_token",
					userInfo.getAccessToken(), maxAge);
			}
		}

		log.debug("🍪 OAuth2 info saved to cookies (expires in {}s)", maxAge);
	}

	// HTTPS 환경 여부 확인 (프로덕션 환경 판별)
	private boolean isSecureEnvironment() {
		// application.yml의 server.ssl.enabled 또는 환경변수로 판별
		String env = System.getProperty("spring.profiles.active", "dev");
		return "prod".equals(env) || "production".equals(env);
	}

	// OAuth2UserPrincipal 추출
	private OAuth2UserPrincipal getOAuth2UserPrincipal(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		return (principal instanceof OAuth2UserPrincipal)
			? (OAuth2UserPrincipal)principal
			: null;
	}

	// 에러 URL 생성
	private String buildErrorUrl(String targetUrl, String errorMessage) {
		return UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam("error", errorMessage)
			.build()
			.toUriString();
	}

	// 인증 관련 쿠키 정리
	protected void clearAuthenticationAttributes(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		super.clearAuthenticationAttributes(request);
		authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

		log.debug("🧹 Authentication cookies cleared");
	}
}