package com.erp.hr.application.port.outbound;

import com.erp.common.domain.Money;

/** 급여 입금 포트 — 현재는 인터페이스만(모킹). 실제 이체 연동은 후속. */
public interface PaymentGateway {
    void deposit(PayoutRequest request);

    /** idempotencyKey 로 중복 입금 방지(codex). */
    record PayoutRequest(Long payrollId, Long employeeId, String period,
                         Money amount, String idempotencyKey) {}
}