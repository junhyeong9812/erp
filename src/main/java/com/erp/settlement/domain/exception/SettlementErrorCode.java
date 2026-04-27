package com.erp.settlement.domain.exception;

import com.erp.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum SettlementErrorCode implements ErrorCode {
    PERIOD_CLOSED("SETTLE-001", "마감된 기간에는 전표를 추가할 수 없습니다", HttpStatus.CONFLICT),
    NO_OPEN_PERIOD("SETTLE-002", "열린 정산 기간이 없습니다", HttpStatus.CONFLICT),
    LEDGER_UNBALANCED("SETTLE-003", "전표 차대변이 일치하지 않습니다", HttpStatus.CONFLICT),
    SELLER_SETTLEMENT_ALREADY_PAID("SETTLE-004", "이미 지급된 정산입니다", HttpStatus.CONFLICT),
    SELLER_NET_NEGATIVE("SETTLE-005", "정산 순지급액이 음수입니다", HttpStatus.CONFLICT),
    REVERSAL_DUPLICATE("SETTLE-006", "이미 반대전표가 발행된 원본입니다", HttpStatus.CONFLICT);

    private final String code; private final String message; private final HttpStatus status;
    SettlementErrorCode(String c, String m, HttpStatus s) { code=c; message=m; status=s; }
    @Override public String code() { return code; }
    @Override public String message() { return message; }
    @Override public HttpStatus status() { return status; }
}
