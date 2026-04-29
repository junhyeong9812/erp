package com.erp.settlement.domain.entity;

import com.erp.common.exception.ConflictException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SettlementPeriodTest {

    @Test
    void open_으로_생성시_상태는_OPEN() {
        SettlementPeriod p = SettlementPeriod.open(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        assertThat(p.getStatus()).isEqualTo(SettlementPeriod.Status.OPEN);
        assertThat(p.isOpen()).isTrue();
    }

    @Test
    void close_호출시_CLOSED_상태로_전이() {
        SettlementPeriod p = SettlementPeriod.open(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));

        p.close();

        assertThat(p.getStatus()).isEqualTo(SettlementPeriod.Status.CLOSED);
        assertThat(p.isOpen()).isFalse();
    }

    @Test
    void OPEN_이_아닌_상태에서_다시_close_하면_예외() {
        SettlementPeriod p = SettlementPeriod.open(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        p.close();

        assertThatThrownBy(p::close)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("OPEN 상태만 마감 가능");
    }

    @Test
    void assertOpen_은_OPEN_에서는_통과() {
        SettlementPeriod p = SettlementPeriod.open(
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));

        // 예외가 발생하지 않아야 함
        p.assertOpen();
    }

    @Test
    void assertOpen_은_CLOSED_에서는_ConflictException() {
        SettlementPeriod p = SettlementPeriod.open(
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 31));
        p.close();

        assertThatThrownBy(p::assertOpen)
                .isInstanceOf(ConflictException.class);
    }

    @Test
    void contains_경계_양끝_포함() {
        SettlementPeriod p = SettlementPeriod.open(
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));

        assertThat(p.contains(LocalDate.of(2026, 4, 1))).isTrue();
        assertThat(p.contains(LocalDate.of(2026, 4, 30))).isTrue();
        assertThat(p.contains(LocalDate.of(2026, 4, 15))).isTrue();
    }

    @Test
    void contains_경계_밖은_거짓() {
        SettlementPeriod p = SettlementPeriod.open(
                LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));

        assertThat(p.contains(LocalDate.of(2026, 3, 31))).isFalse();
        assertThat(p.contains(LocalDate.of(2026, 5, 1))).isFalse();
    }
}