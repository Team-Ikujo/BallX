package com.ballx.exception;

import com.ballx.constants.messages.ErrorCode;

import lombok.Getter;
import lombok.experimental.Accessors;

import org.springframework.core.NestedRuntimeException;

public abstract class BaseException extends NestedRuntimeException {

	@Getter
	@Accessors(fluent = true)
	private ErrorCode error;

	protected BaseException(ErrorCode error) {
		super(error.getMessage());
		this.error = error;
	}

	protected BaseException(ErrorCode error, String message) {
		super(message);
		this.error = error;
	}

	protected BaseException(ErrorCode error, Throwable cause) {
		super(error.getMessage(), cause);
		this.error = error;
	}
}
