package com.erp.promotion.application.usecase;

import com.erp.payment.domain.event.PaymentCompletedEvent;
import com.erp.promotion.application.dto.command.EarnPointCommand;
import com.erp.promotion.application.port.inbound.PointUseCase;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class PointEarningOnPayment {

    private final PointUseCase pointUseCase;

    public PointEarningOnPayment(PointUseCase pointUseCase) { this.pointUseCase = pointUseCase; }

    @ApplicationModuleListener
    public void on(PaymentCompletedEvent event) {
        int reward = (int) (event.amount() * 0.01);  // 1% 적립
        if (reward > 0) {
            pointUseCase.earn(new EarnPointCommand(
                    event.orderId(),   // 실제로는 customerId 로 치환
                    reward,
                    LocalDate.now().plusYears(1)));
        }
    }
}