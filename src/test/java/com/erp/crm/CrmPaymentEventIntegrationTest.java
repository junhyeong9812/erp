package com.erp.crm;

import com.erp.common.domain.Money;
import com.erp.common.messaging.EventBus;
import com.erp.crm.application.dto.command.RegisterCustomerCommand;
import com.erp.crm.application.port.inbound.CustomerUseCase;
import com.erp.crm.application.port.outbound.CustomerRepository;
import com.erp.crm.domain.entity.Customer;
import com.erp.crm.domain.event.CustomerGradeChangedEvent;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CrmPaymentEventIntegrationTest {

    @Autowired CustomerUseCase customerUseCase;
    @Autowired CustomerRepository customerRepository;
    @Autowired EventBus eventBus;
    @Autowired GradeChangeRecorder recorder;

    @Component
    static class GradeChangeRecorder {
        final List<CustomerGradeChangedEvent> events = new ArrayList<>();
        @EventListener
        public void on(CustomerGradeChangedEvent e) { events.add(e); }
    }

    @Test
    void PaymentCompletedEvent_구독_시_해당_주문의_고객_totalPurchase_누적() {
        // given
        recorder.events.clear();
        Long customerId = customerUseCase.register(new RegisterCustomerCommand(
                "C200", "ACME", "-", 1L, 20_000_000L));

        // 전제: Payment → Order → Customer 매핑을 PaymentCompletedEvent 핸들러가 해결한다.
        //       여기서는 orderId 를 customerId 로 사용하는 단순화 (실 구현은 SalesOrderRepository 조회 필요).
        // when
        eventBus.publish(new PaymentCompletedEvent(1L, customerId, 1_200_000L, Instant.now()));

        // then
        Customer c = customerRepository.findById(customerId).orElseThrow();
        assertThat(c.getTotalPurchase()).isEqualTo(Money.of(1_200_000));
        assertThat(c.getGrade()).isEqualTo(Customer.Grade.SILVER);
        assertThat(recorder.events)
                .anyMatch(e -> e.customerId().equals(customerId)
                        && e.newGrade().equals("SILVER"));
    }

    @Test
    void 연속_결제로_누적_매출이_VIP_구간_진입하면_VIP_전환_이벤트() {
        recorder.events.clear();
        Long customerId = customerUseCase.register(new RegisterCustomerCommand(
                "C201", "ACME2", "-", 1L, 50_000_000L));

        eventBus.publish(new PaymentCompletedEvent(1L, customerId, 4_000_000L, Instant.now())); // SILVER
        eventBus.publish(new PaymentCompletedEvent(2L, customerId, 3_000_000L, Instant.now())); // GOLD
        eventBus.publish(new PaymentCompletedEvent(3L, customerId, 5_000_000L, Instant.now())); // VIP

        Customer c = customerRepository.findById(customerId).orElseThrow();
        assertThat(c.getGrade()).isEqualTo(Customer.Grade.VIP);
        assertThat(recorder.events).extracting(CustomerGradeChangedEvent::newGrade)
                .contains("SILVER", "GOLD", "VIP");
    }
}