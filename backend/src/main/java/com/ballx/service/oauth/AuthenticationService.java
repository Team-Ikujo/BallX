package com.ballx.service;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthenticationService {

	// Authentication 객체에서 회원 ID 추출
	public UUID getMemberId(Authentication authentication) {
		validateAuthentication(authentication);

		Object principal = authentication.getPrincipal();
		if (principal instanceof UserDetails) {
			String userId = ((UserDetails)principal).getUsername();
			log.debug("Extracted member ID from authentication - MemberId: {}", userId);
			return UUID.fromString(userId);
		}

		log.error("Invalid principal type - Type: {}", principal.getClass().getName());
		throw new IllegalStateException("인증 정보를 찾을 수 없습니다.");
	}

	private void validateAuthentication(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()) {
			log.warn("Authentication required but not provided");
			throw new IllegalStateException("인증이 필요합니다.");
		}
	}
}