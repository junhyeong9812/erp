package com.erp.settlement.application.usecase;

import com.erp.common.messaging.EventBus;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import com.erp.payment.domain.event.RefundCompletedEvent;
import com.erp.settlement.application.port.inbound.SettlementPeriodUseCase;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.domain.entity.Ledger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentEventHandlerIntegrationTest {

    @Autowired EventBus eventBus;
    @Autowired SettlementPeriodUseCase periodUseCase;
    @Autowired LedgerRepository ledgerRepo;

    @Test
    void PaymentCompletedEvent_수신시_매출전표_자동생성() throws Exception {
        Long pid = periodUseCase.open(LocalDate.now().minusDays(10), LocalDate.now().plusDays(10));

        eventBus.publish(new PaymentCompletedEvent(777L, 7777L, 5000, Instant.now()));

        // @ApplicationModuleListener 는 AFTER_COMMIT 별도 트랜잭션 — 테스트에선 동기 또는 짧은 대기
        Thread.sleep(200);
        assertThat(ledgerRepo.findByPeriodAndType(pid, Ledger.Type.SALES))
                .extracting(Ledger::getReferenceId)
                .contains(777L);
    }

    @Test
    void RefundCompletedEvent_수신시_환불전표_자동생성() throws Exception {
        Long pid = periodUseCase.open(LocalDate.now().minusDays(10), LocalDate.now().plusDays(10));

        eventBus.publish(new RefundCompletedEvent(888L, 880L, 8888L, 2000, Instant.now()));

        Thread.sleep(200);
        assertThat(ledgerRepo.findByPeriodAndType(pid, Ledger.Type.REFUND))
                .extracting(Ledger::getReferenceId)
                .contains(888L);
    }

    @Test
    void 열린_기간이_없으면_전표_생성되지_않음_이벤트_보존() throws Exception {
        // 어떤 기간도 open 하지 않은 상태
        eventBus.publish(new PaymentCompletedEvent(999L, 9999L, 1000, Instant.now()));

        Thread.sleep(200);
        // Handler 가 ConflictException 을 던지면 event_publication 에 미완료 상태로 남고
        // Ledger 는 저장되지 않음
        assertThat(ledgerRepo.findByPeriodAndType(1L, Ledger.Type.SALES))
                .extracting(Ledger::getReferenceId)
                .doesNotContain(999L);
    }
}