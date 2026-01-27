package com.ballx.constants.messages;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import java.text.MessageFormat;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
	// Client Error (4xx)
	BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
	INVALID_DOMAIN_FIELD(HttpStatus.BAD_REQUEST, "도메인 필드 오류 : {0}"),
	BODY_FIELD_ERROR(HttpStatus.BAD_REQUEST, "바디 필드 오류 : {0}"),

	// 타입 관련 Error
	TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "{0} 타입 오류"),
	MISSING_PARAMETER(HttpStatus.BAD_REQUEST, "{0} 파라미터 필요"),
	INVALID_FORMAT(HttpStatus.BAD_REQUEST, "{0} 형식 오류"),

	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생하였습니다. 잠시 후 다시 시도해주세요.");

	private final HttpStatus status;
	private final String message;

	public String format(Object... args) {
		return MessageFormat.format(this.message, args);
	}

	public boolean isSystemError() {
		return status.is5xxServerError();
	}
}
