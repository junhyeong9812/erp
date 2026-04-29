package com.erp.settlement.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.messaging.EventBus;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.application.port.outbound.SellerSettlementRepository;
import com.erp.settlement.domain.entity.Ledger;
import com.erp.settlement.domain.entity.SellerSettlement;
import com.erp.settlement.domain.event.SellerSettlementCalculatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SellerSettlementServiceTest {

    SellerSettlementRepository repo;
    LedgerRepository ledgerRepo;
    EventBus eventBus;
    SellerSettlementService service;

    @BeforeEach
    void setUp() {
        repo = mock(SellerSettlementRepository.class);
        ledgerRepo = mock(LedgerRepository.class);
        eventBus = mock(EventBus.class);
        service = new SellerSettlementService(repo, ledgerRepo, eventBus);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void calculate_는_타입별_집계후_SellerSettlement_저장_이벤트_발행() {
        when(ledgerRepo.findBySellerInPeriod(1L, 1L)).thenReturn(List.of(
                Ledger.sales(1L, Money.of(100_000), "", 1L),
                Ledger.sales(2L, Money.of(50_000), "", 1L),
                Ledger.refund(3L, Money.of(20_000), "", 1L),
                // FEE 전표는 adjustment 로 시뮬레이션 (debit 에 수수료)
                feeLedger(4L, Money.of(5_000), 1L)));

        Long id = service.calculate(1L, 1L);

        assertThat(id).isNotNull();
        ArgumentCaptor<SellerSettlement> cap = ArgumentCaptor.forClass(SellerSettlement.class);
        verify(repo).save(cap.capture());
        SellerSettlement saved = cap.getValue();
        assertThat(saved.getGrossSales()).isEqualTo(Money.of(150_000));
        assertThat(saved.getRefundAmount()).isEqualTo(Money.of(20_000));
        assertThat(saved.getFeeAmount()).isEqualTo(Money.of(5_000));
        assertThat(saved.getNetPayout()).isEqualTo(Money.of(125_000));
        verify(eventBus).publish(any(SellerSettlementCalculatedEvent.class));
    }

    @Test
    void markPaid_는_repo_에서_조회후_상태전이_저장() {
        SellerSettlement s = SellerSettlement.calculate(1L, 1L, Money.of(10_000), Money.ZERO, Money.ZERO);
        s.assignId(42L);
        when(repo.findById(42L)).thenReturn(Optional.of(s));

        service.markPaid(42L);

        assertThat(s.getStatus()).isEqualTo(SellerSettlement.Status.PAID);
        verify(repo).save(s);
    }

    /** FEE 타입 Ledger 헬퍼 — reflection 대신 adjustment 로 debit 만 채움 */
    private Ledger feeLedger(Long ref, Money amount, Long periodId) {
        // 실제 프로덕션에선 Ledger.fee(...) 팩토리 추가를 권장.
        // 여기서는 adjustment(debit only) 로 FEE 집계 분기 테스트 대체 불가하므로
        // 해당 분기는 통합 테스트(또는 실제 FEE 팩토리 추가) 로 검증한다.
        return Ledger.adjustment(ref, "FEE", amount, Money.ZERO, "fee", periodId);
    }
}