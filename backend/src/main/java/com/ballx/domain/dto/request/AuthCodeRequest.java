package com.ballx.domain.dto.request;

import com.ballx.config.validator.ValidMobile;

import jakarta.validation.constraints.NotBlank;

public record AuthCodeRequest(
	@NotBlank(message = "휴대전화 번호는 필수 항목입니다.")
	@ValidMobile
	String mobile
) {
}
