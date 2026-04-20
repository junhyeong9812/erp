package com.erp.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException e) {
        ErrorCode ec = e.errorCode();
        return ResponseEntity.status(ec.status()).body(Map.of(
                "code", ec.code(),
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now()
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.status(CommonErrorCode.INVALID_ARGUMENT.status()).body(Map.of(
                "code", CommonErrorCode.INVALID_ARGUMENT.code(),
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e) {
        return ResponseEntity.status(CommonErrorCode.INTERNAL_ERROR.status()).body(Map.of(
                "code", CommonErrorCode.INTERNAL_ERROR.code(),
                "message", e.getMessage(),
                "timestamp", LocalDateTime.now()
        ));
    }
}
