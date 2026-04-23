package com.erp.payment.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.payment.application.dto.command.RefundCommand;
import com.erp.payment.application.dto.command.RequestPaymentCommand;
import com.erp.payment.application.port.inbound.PaymentUseCase;
import com.erp.payment.application.port.outbound.PaymentGateway;
import com.erp.payment.application.port.outbound.PaymentRepository;
import com.erp.payment.domain.entity.Payment;
import com.erp.payment.domain.entity.Refund;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PaymentService implements PaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentGateway gateway;
    private final EventBus eventBus;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentGateway gateway,
                          EventBus eventBus) {
        this.paymentRepository = paymentRepository;
        this.gateway = gateway;
        this.eventBus = eventBus;
    }

    @Override
    public Long requestPayment(RequestPaymentCommand cmd) {
        Money amount = Money.of(cmd.amount());
        Payment payment = Payment.request(cmd.orderId(), Payment.Method.valueOf(cmd.method()), amount);
        payment.assignId(IdGenerator.next());

        PaymentGateway.Result result = gateway.charge(cmd.orderId(), amount, cmd.method());
        if (result.success()) {
            payment.complete(result.pgTransactionId());
        } else {
            payment.fail(result.message());
        }
        paymentRepository.save(payment);
        eventBus.publishAll(payment.pullEvents());
        return payment.getId();
    }

    @Override
    public Long refund(RefundCommand cmd) {
        Payment payment = paymentRepository.findById(cmd.paymentId())
                .orElseThrow(NotFoundException::new);

        Refund refund = Refund.request(payment.getId(), cmd.orderId(), Money.of(cmd.amount()), cmd.reason());
        refund.assignId(IdGenerator.next());
        refund.complete();   // 실제로는 gateway.refund 호출 후 complete
        eventBus.publishAll(refund.pullEvents());
        return refund.getId();
    }
}