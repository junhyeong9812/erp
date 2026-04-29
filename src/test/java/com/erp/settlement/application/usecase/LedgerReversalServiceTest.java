package com.erp.settlement.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.ConflictException;
import com.erp.settlement.application.port.inbound.SettlementPeriodUseCase;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.domain.entity.Ledger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LedgerReversalServiceTest {

    LedgerRepository ledgerRepo;
    SettlementPeriodUseCase periodUseCase;
    LedgerReversalService service;

    @BeforeEach
    void setUp() {
        ledgerRepo = mock(LedgerRepository.class);
        periodUseCase = mock(SettlementPeriodUseCase.class);
        service = new LedgerReversalService(ledgerRepo, periodUseCase);
        when(ledgerRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void reverse_는_원본_차대변을_뒤집은_REVERSAL_전표_저장() {
        Ledger original = Ledger.sales(1L, Money.of(5000), "s", 1L);
        original.assignId(10L);
        when(ledgerRepo.existsReversalOf(10L)).thenReturn(false);
        when(ledgerRepo.findById(10L)).thenReturn(Optional.of(original));
        when(periodUseCase.currentOpenPeriodId(any(LocalDate.class))).thenReturn(Optional.of(2L));

        Long id = service.reverse(10L, "잘못 기표");

        assertThat(id).isNotNull();
        ArgumentCaptor<Ledger> cap = ArgumentCaptor.forClass(Ledger.class);
        verify(ledgerRepo).save(cap.capture());
        assertThat(cap.getValue().getType()).isEqualTo(Ledger.Type.REVERSAL);
        assertThat(cap.getValue().getDebit()).isEqualTo(Money.of(5000));   // 원본 credit
        assertThat(cap.getValue().getPeriodId()).isEqualTo(2L);           // 현재 열린 기간
    }

    @Test
    void 이미_반대전표_발행된_원본에_재_reverse_하면_ConflictException() {
        when(ledgerRepo.existsReversalOf(10L)).thenReturn(true);

        assertThatThrownBy(() -> service.reverse(10L, "중복"))
                .isInstanceOf(ConflictException.class);
        verify(ledgerRepo, never()).save(any());
    }

    @Test
    void 열린_기간이_없으면_ConflictException() {
        Ledger original = Ledger.sales(1L, Money.of(1000), "s", 1L);
        original.assignId(10L);
        when(ledgerRepo.existsReversalOf(10L)).thenReturn(false);
        when(ledgerRepo.findById(10L)).thenReturn(Optional.of(original));
        when(periodUseCase.currentOpenPeriodId(any(LocalDate.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reverse(10L, "r"))
                .isInstanceOf(ConflictException.class);
        verify(ledgerRepo, never()).save(any());
    }
}