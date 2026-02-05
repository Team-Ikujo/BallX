package com.ballx.security;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.ballx.domain.dto.response.oauth.OAuth2UserInfo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuth2UserPrincipal implements OAuth2User {

	private final OAuth2UserInfo userInfo;
	private final Collection<? extends GrantedAuthority> authorities;

	@Override
	public Map<String, Object> getAttributes() {
		return userInfo.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getName() {
		return userInfo.getProviderId();
	}
}
