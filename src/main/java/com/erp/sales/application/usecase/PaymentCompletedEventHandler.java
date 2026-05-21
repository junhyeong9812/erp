package com.erp.sales.application.usecase;

import com.erp.payment.domain.event.PaymentCompletedEvent;
import com.erp.sales.application.port.inbound.SalesOrderUseCase;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * 결제 완료 이벤트 수신 → 해당 주문을 CONFIRMED 로 전이.
 *
 * <p>주의: payment 모듈의 비공개 도메인 이벤트(`com.erp.payment.domain.event`)를
 * 직접 import 하므로 Spring Modulith 경계 위반이다(설정 모듈 PaymentEventHandler 와 동일 사례).
 * 이 결합은 PHASE04_GAPS.md 에 후속 정리 항목으로 기록되어 있으며,
 * 향후 payment 모듈이 이벤트를 공개 패키지로 노출하거나 shared-kernel 로 옮기면 해소된다.
 */
@Component("salesPaymentCompletedEventHandler")
public class PaymentCompletedEventHandler {

    private final SalesOrderUseCase salesOrderUseCase;

    public PaymentCompletedEventHandler(SalesOrderUseCase salesOrderUseCase) {
        this.salesOrderUseCase = salesOrderUseCase;
    }

    @ApplicationModuleListener
    public void on(PaymentCompletedEvent event) {
        try {
            salesOrderUseCase.confirm(event.orderId());
        } catch (IllegalStateException ignore) {
            // PLACED 외 상태(이미 CONFIRMED/CANCELLED 등) 인 경우는 무시 — 멱등성 보장
        }
    }
}
