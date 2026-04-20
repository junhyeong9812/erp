package com.erp.common.exception;

public class ConflictException extends BusinessException {
    public ConflictException() { super(CommonErrorCode.CONFLICT); }
    public ConflictException(ErrorCode errorCode) { super(errorCode); }
    public ConflictException(ErrorCode errorCode, String detail) { super(errorCode, detail); }
}