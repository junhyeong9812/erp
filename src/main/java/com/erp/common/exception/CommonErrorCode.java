package com.erp.common.exception;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {
    INVALID_ARGUMENT("COMMON-001", "잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
    NOT_FOUND("COMMON-002", "자원을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    CONFLICT("COMMON-003", "상태 충돌이 발생했습니다.", HttpStatus.CONFLICT),
    INTERNAL_ERROR("COMMON-999", "내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    CommonErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    @Override public String code() { return code; }
    @Override public String message() { return message; }
    @Override public HttpStatus status() { return status; }
}