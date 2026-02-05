package com.ballx.domain.dto.response.oauth;

import java.util.UUID;

public record OAuth2MemberResponse(
	UUID id,
	String name,
	String email,
	String mobile
) {
}
