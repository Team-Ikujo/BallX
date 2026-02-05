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
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {

		log.info("=== JWT FILTER STARTED ===");
		String requestURI = request.getRequestURI();
		log.info("Request URI: {}", requestURI);

		// PUBLIC 경로 체크
		boolean isPublicPath = Arrays.stream(SecurityPath.PUBLIC)
			.anyMatch(pattern -> pathMatcher.match(pattern, requestURI));

		log.info("Is Public Path: {}", isPublicPath);

		if (isPublicPath) {
			log.info("✅ Public path - skipping JWT validation");
			filterChain.doFilter(request, response);
			return;
		}

		log.info("🔒 Protected path - validating JWT");

		// JWT 토큰 검증 (Protected 경로만)
		String token = jwtTokenProvider.resolve(request);

		try {
			if (token != null && !token.isEmpty()) {
				jwtTokenProvider.validateToken(token);
				Authentication authenticationToken = jwtTokenProvider.getAuthentication(token);
				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
				log.info("✅ Authentication successful");
			} else {
				log.warn("⚠️ No token provided for protected path");
			}
		} catch (ExpiredJwtException e) {
			log.error("❌ JWT token expired: {}", e.getMessage());
			reject(request, ErrorCode.AUTH_ACCESS_EXPIRED);
		} catch (JwtException | IllegalArgumentException e) {
			log.error("❌ JWT validation failed: {}", e.getMessage());
			reject(request, ErrorCode.AUTH_INVALID);
		}

		filterChain.doFilter(request, response);
	}

	private void reject(HttpServletRequest request, ErrorCode error) {
		CustomException exception = new CustomException(error);
		request.setAttribute("exception", exception);
	}
}