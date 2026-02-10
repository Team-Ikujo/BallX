package com.ballx.exception;

import com.ballx.constants.messages.ErrorCode;

public class CustomException extends BaseException {

	public CustomException(ErrorCode error) {
		super(error);
	}

	public CustomException(ErrorCode error, String message) {
		super(error, error.format(message));
	}

	public CustomException(ErrorCode error, Throwable cause) {
		super(error, cause);
	}

}

