package com.erp.sales.integration;

import com.erp.payment.domain.event.PaymentCompletedEvent;
import com.erp.sales.application.port.inbound.SalesOrderUseCase;
import com.erp.sales.application.usecase.PaymentCompletedEventHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentCompletedEventSubscriptionTest {

    @Mock SalesOrderUseCase salesOrderUseCase;
    @InjectMocks PaymentCompletedEventHandler handler;

    @Test
    void PaymentCompletedEvent_수신_시_해당_주문을_confirm_호출() {
        handler.on(new PaymentCompletedEvent(1L, 10L, 5000L, Instant.now()));

        verify(salesOrderUseCase).confirm(10L);
    }

    @Test
    void 이미_CONFIRMED_등의_상태여서_IllegalStateException_이_나도_무시() {
        doThrow(new IllegalStateException("PLACED 상태만 확정 가능: CONFIRMED"))
                .when(salesOrderUseCase).confirm(99L);

        // throw 가 위로 전파되지 않아야 함 — 멱등성 보장
        handler.on(new PaymentCompletedEvent(1L, 99L, 5000L, Instant.now()));

        verify(salesOrderUseCase).confirm(99L);
    }
}
