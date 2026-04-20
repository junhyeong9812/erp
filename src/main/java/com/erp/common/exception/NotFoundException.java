package com.erp.common.exception;

public class NotFoundException extends BusinessException {
    public NotFoundException() { super(CommonErrorCode.NOT_FOUND); }
    public NotFoundException(ErrorCode errorCode) { super(errorCode); }
    public NotFoundException(ErrorCode errorCode, String detail) { super(errorCode, detail); }
}