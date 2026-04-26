package com.erp.payment.infrastructure.external;

import com.erp.common.domain.Money;
import com.erp.payment.application.port.outbound.PaymentGateway;
import org.springframework.stereotype.Component;

import java.util.UUID;

/** 학습용 페이크 PG. 항상 성공. */
@Component
public class FakePaymentGateway implements PaymentGateway {

    @Override
    public Result charge(Long orderId, Money amount, String method) {
        return new Result(true, "FAKE-" + UUID.randomUUID(), "ok");
    }

    @Override
    public Result refund(String pgTxId, Money amount) {
        return new Result(true, "REFUND-" + UUID.randomUUID(), "ok");
    }
}