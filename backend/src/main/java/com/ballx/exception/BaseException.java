package com.ballx.exception;

import com.ballx.constants.messages.ErrorCode;

import lombok.Getter;
import lombok.experimental.Accessors;

import org.springframework.core.NestedRuntimeException;

public class BaseException extends NestedRuntimeException {

	@Getter
	@Accessors(fluent = true)
	private ErrorCode error;

	public BaseException(ErrorCode error) {
		super(error.name());
		this.error = error;
	}
}
