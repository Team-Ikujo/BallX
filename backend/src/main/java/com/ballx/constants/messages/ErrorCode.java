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

	AUTH_INVALID_ACCESS_PATH(HttpStatus.UNAUTHORIZED, "올바르지 않은 접근 경로입니다."),
	AUTH_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
	AUTH_INVALID(HttpStatus.UNAUTHORIZED, "올바르지 않은 인증 정보입니다."),
	AUTH_ACCESS_EXPIRED(HttpStatus.UNAUTHORIZED, "엑세스 토큰이 만료되었습니다."),
	AUTH_REFRESH_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다. 재로그인이 필요합니다."),
	AUTH_USER_INACTIVE(HttpStatus.FORBIDDEN,"사용자 계정이 비활성화되었습니다."),
	AUTH_DUPLICATE_ACCOUNT(HttpStatus.CONFLICT,"이미 해당 전화번호로 가입된 계정이 존재합니다."),
	AUTH_INVALID_DEVICE_ID(HttpStatus.BAD_REQUEST, "deviceId는 필수입니다."),

	// User Error
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 유저를 찾을 수 없습니다."),

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
