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

	@NotNull(message = "MALE , FEMALE")
	Gender gender,

	@NotNull
	@Past // 과거여야 함
	LocalDate birthDate
) {

}
