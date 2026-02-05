package com.ballx.service.oauth;

import org.springframework.stereotype.Service;

import com.ballx.constants.ProviderType;
import com.ballx.domain.dto.response.oauth.OAuth2InfoResponse;
import com.ballx.domain.dto.response.oauth.OAuth2UserInfo;
import com.ballx.utils.CookieUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OAuth2CookieService {

	private static final String OAUTH2_PROVIDER_COOKIE = "oauth2_provider";
	private static final String OAUTH2_PROVIDER_ID_COOKIE = "oauth2_provider_id";
	private static final String OAUTH2_EMAIL_COOKIE = "oauth2_email";
	private static final String OAUTH2_ACCESS_TOKEN_COOKIE = "oauth2_access_token";
	private static final int COOKIE_EXPIRE_SECONDS = 300;

	public void saveOAuth2InfoToCookie(HttpServletResponse response, OAuth2UserInfo userInfo) {
		CookieUtils.addCookie(response, OAUTH2_PROVIDER_COOKIE,
			userInfo.getProvider().name(), COOKIE_EXPIRE_SECONDS);
		CookieUtils.addCookie(response, OAUTH2_PROVIDER_ID_COOKIE,
			userInfo.getProviderId(), COOKIE_EXPIRE_SECONDS);
		CookieUtils.addCookie(response, OAUTH2_EMAIL_COOKIE,
			userInfo.getEmail(), COOKIE_EXPIRE_SECONDS);
		CookieUtils.addCookie(response, OAUTH2_ACCESS_TOKEN_COOKIE,
			userInfo.getAccessToken(), COOKIE_EXPIRE_SECONDS);
	}

	public OAuth2InfoResponse getOAuth2InfoFromCookie(HttpServletRequest request) {
		String provider = getCookieValue(request, OAUTH2_PROVIDER_COOKIE);
		String providerId = getCookieValue(request, OAUTH2_PROVIDER_ID_COOKIE);
		String email = getCookieValue(request, OAUTH2_EMAIL_COOKIE);
		String accessToken = CookieUtils.getCookie(request, OAUTH2_ACCESS_TOKEN_COOKIE)
			.map(cookie -> cookie.getValue())
			.orElse(null);

		return new OAuth2InfoResponse(
			ProviderType.valueOf(provider),
			providerId,
			email,
			accessToken
		);
	}

	public void deleteOAuth2InfoCookie(HttpServletRequest request, HttpServletResponse response) {
		CookieUtils.deleteCookie(request, response, OAUTH2_PROVIDER_COOKIE);
		CookieUtils.deleteCookie(request, response, OAUTH2_PROVIDER_ID_COOKIE);
		CookieUtils.deleteCookie(request, response, OAUTH2_EMAIL_COOKIE);
		CookieUtils.deleteCookie(request, response, OAUTH2_ACCESS_TOKEN_COOKIE);
	}

	private String getCookieValue(HttpServletRequest request, String cookieName) {
		return CookieUtils.getCookie(request, cookieName)
			.map(cookie -> cookie.getValue())
			.orElseThrow(() -> new IllegalStateException(
				"OAuth2 인증 정보가 없습니다. 다시 로그인해주세요."));
	}
}