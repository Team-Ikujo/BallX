package com.ballx.exception;

import com.ballx.constants.messages.ErrorCode;

import lombok.Getter;

public class FieldValidationException extends BaseException {

	@Getter
	private final String message;

	public FieldValidationException(ErrorCode error, String message) {
		super(error);
		this.message = error.format(message);
	}
}
