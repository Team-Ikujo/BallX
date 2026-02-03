package com.ballx.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class AuthRequest {
	public record LoginRequest(
		@NotBlank(message = "mobile은 필수입니다.")
		@Pattern(
			regexp = "^01[016789]\\d{7,8}$",
			message = "mobile 형식이 올바르지 않습니다. 예) 01012345678"
		)
		String mobile
	) {}
}
