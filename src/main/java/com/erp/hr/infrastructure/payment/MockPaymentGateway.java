package com.erp.hr.infrastructure.payment;

import com.erp.hr.application.port.outbound.PaymentGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/** 실제 이체 없이 로그만. idempotencyKey 로 중복 입금을 거른다(codex). */
@Component
public class MockPaymentGateway implements PaymentGateway {

    private static final Logger log = LoggerFactory.getLogger(MockPaymentGateway.class);
    private final Set<String> processed = ConcurrentHashMap.newKeySet();

    @Override
    public void deposit(PayoutRequest req) {
        if (!processed.add(req.idempotencyKey())) {
            log.warn("[MOCK] 중복 입금 무시: {}", req.idempotencyKey());
            return;
        }
        log.info("[MOCK] 입금 emp={} period={} amount={} key={}",
                req.employeeId(), req.period(), req.amount(), req.idempotencyKey());
    }
}