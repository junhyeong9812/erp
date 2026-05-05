package com.erp.settlement.application.usecase;

import com.erp.common.messaging.EventBus;
import com.erp.common.persistence.InMemoryRepository;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import com.erp.payment.domain.event.RefundCompletedEvent;
import com.erp.settlement.application.port.inbound.SettlementPeriodUseCase;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.application.port.outbound.SettlementPeriodRepository;
import com.erp.settlement.domain.entity.Ledger;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentEventHandlerIntegrationTest {

    @Autowired EventBus eventBus;
    @Autowired SettlementPeriodUseCase periodUseCase;
    @Autowired LedgerRepository ledgerRepo;
    @Autowired SettlementPeriodRepository periodRepo;
    @Autowired PlatformTransactionManager txManager;

    @AfterEach
    void cleanInMemoryStores() {
        // @SpringBootTest 는 InMemoryRepository(Map) 의 상태를 롤백하지 않으므로
        // 매 테스트 후 명시 초기화 — 그렇지 않으면 이전 테스트의 period 가 남아 있어
        // listener 가 잘못된 period 에 ledger 를 생성한다.
        ((InMemoryRepository<?, ?>) periodRepo).clear();
        ((InMemoryRepository<?, ?>) ledgerRepo).clear();
    }

    /** AFTER_COMMIT phase 리스너는 활성 TX 안에서 발행되어야 발화한다. */
    private void publishInTx(com.erp.common.domain.DomainEvent event) {
        new TransactionTemplate(txManager).executeWithoutResult(status -> eventBus.publish(event));
    }

    @Test
    void PaymentCompletedEvent_수신시_매출전표_자동생성() {
        Long pid = periodUseCase.open(LocalDate.now().minusDays(10), LocalDate.now().plusDays(10));

        publishInTx(new PaymentCompletedEvent(777L, 7777L, 5000, Instant.now()));

        // @ApplicationModuleListener 는 AFTER_COMMIT 별도 비동기 — 폴링으로 안정 대기
        Awaitility.await().atMost(Duration.ofSeconds(3))
                .untilAsserted(() ->
                        assertThat(ledgerRepo.findByPeriodAndType(pid, Ledger.Type.SALES))
                                .extracting(Ledger::getReferenceId)
                                .contains(777L));
    }

    @Test
    void RefundCompletedEvent_수신시_환불전표_자동생성() {
        Long pid = periodUseCase.open(LocalDate.now().minusDays(10), LocalDate.now().plusDays(10));

        publishInTx(new RefundCompletedEvent(888L, 880L, 8888L, 2000, Instant.now()));

        Awaitility.await().atMost(Duration.ofSeconds(3))
                .untilAsserted(() ->
                        assertThat(ledgerRepo.findByPeriodAndType(pid, Ledger.Type.REFUND))
                                .extracting(Ledger::getReferenceId)
                                .contains(888L));
    }

    @Test
    void 열린_기간이_없으면_전표_생성되지_않음_이벤트_보존() throws Exception {
        publishInTx(new PaymentCompletedEvent(999L, 9999L, 1000, Instant.now()));

        // 핸들러가 ConflictException 을 던져 Ledger 는 저장되지 않아야 한다.
        Thread.sleep(500);
        assertThat(ledgerRepo.findByPeriodAndType(1L, Ledger.Type.SALES))
                .extracting(Ledger::getReferenceId)
                .doesNotContain(999L);
    }
}
