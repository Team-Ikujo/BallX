package com.ballx.domain.dto.request.oauth;

import java.time.LocalDate;

import com.ballx.constants.Gender;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

public record OAuth2SignUpRequest(
	@NotBlank
	String name,

	@NotBlank
	@Pattern(regexp = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$")
	String mobile,

	@NotNull(message = "성별은 필수 항목 입니다")
	Gender gender,

	@NotNull
	@Past
	LocalDate birthDate
) {

}
