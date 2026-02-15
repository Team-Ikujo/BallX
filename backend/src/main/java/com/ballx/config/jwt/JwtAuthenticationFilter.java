package com.ballx.config.jwt;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.CustomException;
import com.ballx.security.SecurityPath;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenProvider jwtTokenProvider;

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		String requestURI = request.getRequestURI();

		boolean isPublic = Arrays.stream(SecurityPath.PUBLIC)
			.anyMatch(pattern -> matches(pattern, requestURI));

		if (isPublic) {
			filterChain.doFilter(request, response);
			return;
		}

		String token = jwtTokenProvider.resolve(request);

		try {
			if (token != null && !token.isEmpty()) {
				jwtTokenProvider.validateToken(token);
				Authentication authenticationToken = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}
		} catch (ExpiredJwtException e) {
			reject(request, ErrorCode.AUTH_ACCESS_EXPIRED);
		} catch (JwtException | IllegalArgumentException e) {
			reject(request, ErrorCode.AUTH_INVALID);
		}
		filterChain.doFilter(request, response);
	}

	private boolean matches(String pattern, String path) {
		AntPathMatcher pathMatcher = new AntPathMatcher();
		return pathMatcher.match(pattern, path);
	}

	private void reject(HttpServletRequest request, ErrorCode error) {
		CustomException exception = new CustomException(error);
		request.setAttribute("exception", exception);
		throw exception;
	}
}