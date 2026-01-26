package com.ballx.validation;

import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.FieldValidationException;

public final class Preconditions {

	public static void domainValidate(boolean expression, String message) {
		if (!expression) {
			throw new FieldValidationException(ErrorCode.INVALID_DOMAIN_FIELD, message);
		}
	}
}
