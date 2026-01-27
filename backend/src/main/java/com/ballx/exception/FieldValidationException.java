package com.ballx.exception;

import com.ballx.constants.messages.ErrorCode;

import lombok.Getter;

public class FieldValidationException extends BaseException {

	@Getter
	private final String field;

	public FieldValidationException(String field, ErrorCode error) {
		super(error);
		this.field = field;
	}
}
