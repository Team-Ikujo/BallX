package com.ballx.exception.handler;

import com.ballx.common.api.ApiErrorResponse;

import com.ballx.constants.messages.ErrorCode;
import com.ballx.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Slf4j
public class BaseExceptionHandler {

	public ResponseEntity<ApiErrorResponse> toResponse(BaseException ex) {
		HttpStatus status = ex.error().getStatus();
		String message = ex.error().getMessage();
		return toResponse(status, message);
	}

	public ResponseEntity<ApiErrorResponse> toResponse(ErrorCode errorCode) {
		HttpStatus status = errorCode.getStatus();
		String message = errorCode.getMessage();
		return toResponse(status, message);
	}

	public ResponseEntity<ApiErrorResponse> toResponse(ErrorCode errorCode, Object... args) {
		HttpStatus status = errorCode.getStatus();
		String message = errorCode.format(args);
		return toResponse(status, message);
	}

	public ResponseEntity<ApiErrorResponse> toResponse(HttpStatus status, String message) {
		return ApiErrorResponse.error(status, message);
	}
}
