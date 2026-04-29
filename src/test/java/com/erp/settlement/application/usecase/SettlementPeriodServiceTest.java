package com.erp.settlement.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.ConflictException;
import com.erp.common.messaging.EventBus;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.application.port.outbound.SettlementPeriodRepository;
import com.erp.settlement.domain.entity.Ledger;
import com.erp.settlement.domain.entity.SettlementPeriod;
import com.erp.settlement.domain.event.LedgerUnbalancedEvent;
import com.erp.settlement.domain.event.PeriodClosedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SettlementPeriodServiceTest {

    SettlementPeriodRepository periodRepo;
    LedgerRepository ledgerRepo;
    EventBus eventBus;
    SettlementPeriodService service;

    @BeforeEach
    void setUp() {
        periodRepo = mock(SettlementPeriodRepository.class);
        ledgerRepo = mock(LedgerRepository.class);
        eventBus = mock(EventBus.class);
        service = new SettlementPeriodService(periodRepo, ledgerRepo, eventBus);
        when(periodRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void open_은_OPEN_기간_저장후_id_반환() {
        Long id = service.open(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));

        assertThat(id).isNotNull();
        verify(periodRepo).save(any(SettlementPeriod.class));
    }

    @Test
    void close_는_reconciliation_통과시_CLOSED_전이_이벤트_발행() {
        SettlementPeriod p = SettlementPeriod.open(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));
        p.assignId(1L);
        when(periodRepo.findById(1L)).thenReturn(Optional.of(p));
        when(ledgerRepo.findByPeriodId(1L)).thenReturn(List.of(
                Ledger.sales(1L, Money.of(1000), "", 1L)));

        service.close(1L);

        assertThat(p.getStatus()).isEqualTo(SettlementPeriod.Status.CLOSED);
        verify(eventBus).publish(any(PeriodClosedEvent.class));
    }

    @Test
    void close_는_reconciliation_실패시_Unbalanced_이벤트_발행_후_예외() {
        SettlementPeriod p = SettlementPeriod.open(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));
        p.assignId(1L);
        when(periodRepo.findById(1L)).thenReturn(Optional.of(p));
        when(ledgerRepo.findByPeriodId(1L)).thenReturn(List.of(
                Ledger.adjustment(10L, "INV", Money.of(500), Money.of(400), "오기표", 1L)));

        assertThatThrownBy(() -> service.close(1L))
                .isInstanceOf(ConflictException.class);

        verify(eventBus).publish(any(LedgerUnbalancedEvent.class));
        verify(eventBus, never()).publish(any(PeriodClosedEvent.class));
        assertThat(p.getStatus()).isEqualTo(SettlementPeriod.Status.OPEN);   // 전이되지 않음
    }

    @Test
    void currentOpenPeriodId_는_Repository_결과를_Optional_id_로_매핑() {
        SettlementPeriod p = SettlementPeriod.open(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));
        p.assignId(7L);
        when(periodRepo.findOpenContaining(LocalDate.of(2026, 4, 15))).thenReturn(Optional.of(p));

        Optional<Long> id = service.currentOpenPeriodId(LocalDate.of(2026, 4, 15));

        assertThat(id).contains(7L);
    }

    @Test
    void currentOpenPeriodId_는_열린_기간_없으면_empty() {
        when(periodRepo.findOpenContaining(any())).thenReturn(Optional.empty());

        assertThat(service.currentOpenPeriodId(LocalDate.now())).isEmpty();
    }
}