package com.erp.payment.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.messaging.EventBus;
import com.erp.payment.application.dto.command.RequestPaymentCommand;
import com.erp.payment.application.port.outbound.PaymentGateway;
import com.erp.payment.application.port.outbound.PaymentRepository;
import com.erp.payment.domain.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;

class PaymentServiceTest {

    private PaymentRepository repo;
    private PaymentGateway gateway;
    private EventBus eventBus;
    private PaymentService service;

    @BeforeEach
    void setUp() {
        repo = mock(PaymentRepository.class);
        gateway = mock(PaymentGateway.class);
        eventBus = mock(EventBus.class);
        service = new PaymentService(repo, gateway, eventBus);
    }

    @Test
    void 결제_성공_시_complete_호출_이벤트_발행() {
        given(gateway.charge(anyLong(), any(Money.class), anyString()))
                .willReturn(new PaymentGateway.Result(true, "PG-123", "ok"));
        given(repo.save(any(Payment.class))).willAnswer(inv -> inv.getArgument(0));

        service.requestPayment(new RequestPaymentCommand(1L, "CARD", 1000));

        then(eventBus).should().publishAll(argThat(evts -> !evts.isEmpty()));
    }
}