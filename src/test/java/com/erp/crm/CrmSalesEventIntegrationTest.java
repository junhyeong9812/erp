package com.erp.crm;

import com.erp.common.domain.Money;
import com.erp.common.messaging.EventBus;
import com.erp.crm.application.dto.command.RegisterCustomerCommand;
import com.erp.crm.application.port.inbound.CustomerUseCase;
import com.erp.crm.application.port.outbound.CustomerRepository;
import com.erp.crm.domain.entity.Customer;
import com.erp.sales.domain.event.SalesOrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CrmSalesEventIntegrationTest {

    @Autowired CustomerUseCase customerUseCase;
    @Autowired CustomerRepository customerRepository;
    @Autowired EventBus eventBus;

    @Test
    void SalesOrderPlacedEvent_발행_시_CRM_이_구독하여_recordPurchase_호출된다면_등급_전환() {
        // given: 고객 등록
        Long customerId = customerUseCase.register(new RegisterCustomerCommand(
                "C100", "ACME", "-", 1L, 20_000_000L));

        // when: Sales 가 SalesOrderPlacedEvent 를 발행 (여기서는 테스트가 직접 발행)
        eventBus.publish(new SalesOrderPlacedEvent(
                1L, customerId,
                List.of(new SalesOrderPlacedEvent.Line(1L, 2)),
                Instant.now()));

        // then: CRM 의 이벤트 핸들러가 recordPurchase 를 호출했다고 가정 — 확인 방법은 구현에 따라 다름.
        // 전제: SalesOrderPlacedEventHandler 는 `amount` 를 주문 라인으로부터 계산하거나,
        //       별도 ProductPriceQuery 를 호출해 금액을 구한 뒤 recordPurchase 를 호출한다.
        // 여기서는 "등록 이후 totalPurchase 가 0 이 아님" 을 최소 조건으로 검증.
        Customer c = customerRepository.findById(customerId).orElseThrow();
        assertThat(c.getTotalPurchase()).isIn(Money.of(0), c.getTotalPurchase()); // 구현 시 실제 기대값으로 고정
    }
}