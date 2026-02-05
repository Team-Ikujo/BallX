package com.ballx.config.oauth;

import static com.ballx.config.oauth.HttpCookieOAuth2AuthorizationRequestRepository.*;

import java.io.IOException;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.ballx.utils.CookieUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

	private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

	@Override
	public void onAuthenticationFailure(
		HttpServletRequest request,
		HttpServletResponse response,
		AuthenticationException exception
	) throws IOException {

		String targetUrl = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
			.map(Cookie::getValue)
			.orElse("/");

		log.error("OAuth2 Authentication Failed: {}", exception.getMessage());

		targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
			.queryParam("error", exception.getLocalizedMessage())
			.build().toUriString();

		httpCookieOAuth2AuthorizationRequestRepository
			.removeAuthorizationRequestCookies(request, response);

		getRedirectStrategy().sendRedirect(request, response, targetUrl);
	}
}
