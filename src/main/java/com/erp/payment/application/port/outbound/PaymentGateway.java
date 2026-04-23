package com.erp.payment.application.port.outbound;

import com.erp.common.domain.Money;

public interface PaymentGateway {
    Result charge(Long orderId, Money amount, String method);
    Result refund(String pgTxId, Money amount);

    record Result(boolean success, String pgTransactionId, String message) {}
}