package com.ballx.exception.handler;

import com.ballx.common.api.ApiErrorResponse;
import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.CustomException;

import com.ballx.exception.FieldValidationException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SystemExceptionHandler extends BaseExceptionHandler {

	/**
	 * 비즈니스/도메인 로직에서 발생하는 모든 예외
	 */
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ApiErrorResponse> handleCustomException(CustomException ex) {
		ErrorCode error = ex.error();
		if (error.isSystemError()) {
			log.error("[System Error] code={} message={}", error.name(), error.getMessage(), ex);
		} else {
			log.warn("[Business Error] code={} message={}", error.name(), error.getMessage());
		}
		return toResponse(ex);
	}

	/**
	 * 필드 검증 예외 처리
	 */
	@ExceptionHandler(FieldValidationException.class)
	public ResponseEntity<ApiErrorResponse> handleFieldValidation(FieldValidationException ex) {
		log.warn("[Field Validation] field={} code={} message={}",
			ex.getField(),
			ex.error().name(),
			ex.error().getMessage()
		);
		return toResponse(ex.error(), ex.getField());
	}
}
