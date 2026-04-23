package com.erp.payment.application.port.inbound;

import com.erp.payment.application.dto.command.RequestPaymentCommand;
import com.erp.payment.application.dto.command.RefundCommand;

public interface PaymentUseCase {
    Long requestPayment(RequestPaymentCommand command);
    Long refund(RefundCommand command);
}