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

		String targetUrl = determineTargetUrl(request, response, authentication);

		if (response.isCommitted()) {
			log.warn("⚠️ Response already committed. Cannot redirect to {}", targetUrl);
			return;
		}

		clearAuthenticationAttributes(request, response);

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}

	protected String determineTargetUrl(
		HttpServletRequest request,
		HttpServletResponse response,
		Authentication authentication
	) {
		Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
			.map(Cookie::getValue);

		if (redirectUri.isPresent()) {
			String uri = redirectUri.get();
			if (!oAuth2Properties.isAuthorizedRedirectUri(uri)) {
				log.error("🚨 Unauthorized Redirect URI: {}", uri);
				throw new CustomException(ErrorCode.UNAUTHORIZED_REDIRECT_URI);
			}
		}

		String mode = CookieUtils.getCookie(request, MODE_PARAM_COOKIE_NAME)
			.map(Cookie::getValue)
			.orElse("login");

		String targetUrl = redirectUri.orElse(
			"login".equalsIgnoreCase(mode)
				? oAuth2Properties.defaultRedirectUri()
				: "/api/auth/oauth2/unlink"
		);

		OAuth2UserPrincipal principal = getOAuth2UserPrincipal(authentication);
		if (principal == null) {
			log.error("🚨 OAuth2UserPrincipal is null");
			return buildErrorUrl(targetUrl, "Authentication failed");
		}

		OAuth2UserInfo userInfo = principal.getUserInfo();

		switch (mode.toLowerCase()) {
			case "login":
				return handleLoginMode(response, targetUrl, userInfo);
			case "unlink":
				return handleUnlinkMode(response, targetUrl, userInfo);
			default:
				log.warn(" Unknown mode: {}, defaulting to login", mode);
				return handleLoginMode(response, targetUrl, userInfo);
		}
	}

	private String handleLoginMode(
		HttpServletResponse response,
		String targetUrl,
		OAuth2UserInfo userInfo
	) {
		saveOAuth2InfoToCookie(response, userInfo);

		log.info("   OAuth2 Login Success");
		log.info("   Provider: {}", userInfo.getProvider());
		log.info("   Email: {}", userInfo.getEmail());
		log.info("   Provider ID: {}", userInfo.getProviderId());

		return UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam("provider", userInfo.getProvider().name())
			.build()
			.toUriString();
	}

	private String handleUnlinkMode(
		HttpServletResponse response,
		String targetUrl,
		OAuth2UserInfo userInfo
	) {
		saveOAuth2InfoToCookie(response, userInfo);

		log.info("   OAuth2 Unlink Request");
		log.info("   Provider: {}", userInfo.getProvider());
		log.info("   Provider ID: {}", userInfo.getProviderId());
		log.info("   Email: {}", userInfo.getEmail());

		return targetUrl;
	}

	private void saveOAuth2InfoToCookie(HttpServletResponse response, OAuth2UserInfo userInfo) {
		int maxAge = 300; // 5분

		CookieUtils.addCookie(response, "oauth2_provider",
			userInfo.getProvider().name(), maxAge);
		CookieUtils.addCookie(response, "oauth2_provider_id",
			userInfo.getProviderId(), maxAge);
		CookieUtils.addCookie(response, "oauth2_email",
			userInfo.getEmail(), maxAge);

		if (userInfo.getAccessToken() != null) {
			if (isSecureEnvironment()) {
				CookieUtils.addSecureCookie(response, "oauth2_access_token",
					userInfo.getAccessToken(), maxAge);
			} else {
				CookieUtils.addCookie(response, "oauth2_access_token",
					userInfo.getAccessToken(), maxAge);
			}
		}
	}

	private boolean isSecureEnvironment() {
		String env = System.getProperty("spring.profiles.active", "dev");
		return "prod".equals(env) || "production".equals(env);
	}

	private OAuth2UserPrincipal getOAuth2UserPrincipal(Authentication authentication) {
		Object principal = authentication.getPrincipal();
		return (principal instanceof OAuth2UserPrincipal)
			? (OAuth2UserPrincipal)principal
			: null;
	}

	private String buildErrorUrl(String targetUrl, String errorMessage) {
		return UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam("error", errorMessage)
			.build()
			.toUriString();
	}

	protected void clearAuthenticationAttributes(
		HttpServletRequest request,
		HttpServletResponse response
	) {
		super.clearAuthenticationAttributes(request);
		authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
	}
}