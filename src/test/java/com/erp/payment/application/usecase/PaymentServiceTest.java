package com.erp.payment.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.payment.application.dto.command.RefundCommand;
import com.erp.payment.application.dto.command.RequestPaymentCommand;
import com.erp.payment.application.port.outbound.PaymentGateway;
import com.erp.payment.application.port.outbound.PaymentRepository;
import com.erp.payment.domain.entity.Payment;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

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

    @Test
    void 결제_실패_시_FAILED_이며_PaymentCompletedEvent_발행되지_않음() {
        given(gateway.charge(anyLong(), any(Money.class), anyString()))
                .willReturn(new PaymentGateway.Result(false, null, "declined"));
        given(repo.save(any(Payment.class))).willAnswer(inv -> inv.getArgument(0));

        service.requestPayment(new RequestPaymentCommand(1L, "CARD", 1000));

        ArgumentCaptor<Payment> captor = ArgumentCaptor.forClass(Payment.class);
        then(repo).should().save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(Payment.Status.FAILED);

        // publishAll 호출되더라도 PaymentCompletedEvent 는 포함되지 않아야 함
        then(eventBus).should().publishAll(argThat(evts ->
                evts.stream().noneMatch(PaymentCompletedEvent.class::isInstance)));
    }

    @Test
    void Gateway_는_Command_의_파라미터_그대로_호출() {
        given(gateway.charge(anyLong(), any(Money.class), anyString()))
                .willReturn(new PaymentGateway.Result(true, "PG-1", "ok"));
        given(repo.save(any(Payment.class))).willAnswer(inv -> inv.getArgument(0));

        service.requestPayment(new RequestPaymentCommand(7L, "BANK", 2500));

        then(gateway).should().charge(7L, Money.of(2500), "BANK");
    }

    @Test
    void 환불_성공_시_Refund_저장되고_이벤트_발행() {
        Payment payment = Payment.request(1L, Payment.Method.CARD, Money.of(1000));
        payment.assignId(10L);
        given(repo.findById(10L)).willReturn(Optional.of(payment));

        service.refund(new RefundCommand(10L, 1L, 300, "고객요청"));

        then(eventBus).should().publishAll(argThat(evts -> !evts.isEmpty()));
    }

    @Test
    void 존재하지_않는_Payment_환불_시_NotFoundException() {
        given(repo.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.refund(new RefundCommand(999L, 1L, 100, "x")))
                .isInstanceOf(NotFoundException.class);

        then(eventBus).should(never()).publishAll(any(List.class));
    }
}