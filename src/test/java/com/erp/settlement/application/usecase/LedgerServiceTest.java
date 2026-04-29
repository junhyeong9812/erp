package com.erp.settlement.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.ConflictException;
import com.erp.settlement.application.dto.command.CreateLedgerCommand;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.application.port.outbound.SettlementPeriodRepository;
import com.erp.settlement.domain.entity.Ledger;
import com.erp.settlement.domain.entity.SettlementPeriod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LedgerServiceTest {

    LedgerRepository ledgerRepo;
    SettlementPeriodRepository periodRepo;
    LedgerService service;

    @BeforeEach
    void setUp() {
        ledgerRepo = mock(LedgerRepository.class);
        periodRepo = mock(SettlementPeriodRepository.class);
        service = new LedgerService(ledgerRepo, periodRepo);
        when(ledgerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void createSalesLedger_는_저장후_id_반환() {
        SettlementPeriod open = SettlementPeriod.open(LocalDate.now().minusDays(10), LocalDate.now().plusDays(10));
        open.assignId(1L);
        when(periodRepo.findById(1L)).thenReturn(Optional.of(open));

        Long id = service.createSalesLedger(new CreateLedgerCommand(100L, 5000, "order", 1L));

        assertThat(id).isNotNull();
        ArgumentCaptor<Ledger> cap = ArgumentCaptor.forClass(Ledger.class);
        verify(ledgerRepo).save(cap.capture());
        assertThat(cap.getValue().getType()).isEqualTo(Ledger.Type.SALES);
        assertThat(cap.getValue().getCredit()).isEqualTo(Money.of(5000));
    }

    @Test
    void 마감된_기간에_매출전표_생성시_ConflictException() {
        SettlementPeriod closed = SettlementPeriod.open(LocalDate.now().minusDays(40), LocalDate.now().minusDays(10));
        closed.assignId(1L);
        closed.close();
        when(periodRepo.findById(1L)).thenReturn(Optional.of(closed));

        assertThatThrownBy(() ->
                service.createSalesLedger(new CreateLedgerCommand(100L, 5000, "", 1L)))
                .isInstanceOf(ConflictException.class);
        verify(ledgerRepo, never()).save(any());
    }

    @Test
    void createRefundLedger_도_assertOpen_통과해야_저장() {
        SettlementPeriod open = SettlementPeriod.open(LocalDate.now().minusDays(10), LocalDate.now().plusDays(10));
        open.assignId(1L);
        when(periodRepo.findById(1L)).thenReturn(Optional.of(open));

        service.createRefundLedger(new CreateLedgerCommand(200L, 2000, "r", 1L));

        ArgumentCaptor<Ledger> cap = ArgumentCaptor.forClass(Ledger.class);
        verify(ledgerRepo).save(cap.capture());
        assertThat(cap.getValue().getType()).isEqualTo(Ledger.Type.REFUND);
        assertThat(cap.getValue().getDebit()).isEqualTo(Money.of(2000));
    }

    @Test
    void totalSales_는_해당_기간_SALES_전표_credit_합산() {
        when(ledgerRepo.findByPeriodAndType(1L, Ledger.Type.SALES)).thenReturn(List.of(
                Ledger.sales(1L, Money.of(1000), "", 1L),
                Ledger.sales(2L, Money.of(2000), "", 1L),
                Ledger.sales(3L, Money.of(500), "", 1L)));

        Money total = service.totalSales(1L);

        assertThat(total).isEqualTo(Money.of(3500));
    }
}