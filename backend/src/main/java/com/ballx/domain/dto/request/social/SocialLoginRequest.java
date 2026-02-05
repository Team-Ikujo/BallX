package com.ballx.domain.dto.request.social;

import jakarta.validation.constraints.NotBlank;

public record SocialLoginRequest (
	@NotBlank
	String code
){
}
