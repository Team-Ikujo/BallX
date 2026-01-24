package com.ballx.exception;

import com.ballx.constants.messages.ErrorCode;

public class CustomException extends BaseException {

	public CustomException(ErrorCode error) {
		super(error);
	}

}

