package com.erp.common.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PeriodTest {

    @Test
    void end_가_start_보다_빠르면_예외() {
        assertThatThrownBy(() -> new Period(
                LocalDate.of(2026, 5, 1),
                LocalDate.of(2026, 4, 30)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("end before start");
    }

    @Test
    void start_과_end_가_같은_날은_허용() {
        Period p = new Period(LocalDate.of(2026, 4, 20), LocalDate.of(2026, 4, 20));
        assertThat(p.contains(LocalDate.of(2026, 4, 20))).isTrue();
    }

    @Test
    void contains_경계_양끝_포함() {
        Period p = new Period(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));
        assertThat(p.contains(LocalDate.of(2026, 4, 1))).isTrue();    // start 포함
        assertThat(p.contains(LocalDate.of(2026, 4, 30))).isTrue();   // end 포함
        assertThat(p.contains(LocalDate.of(2026, 4, 15))).isTrue();   // 중간
    }

    @Test
    void contains_경계_밖_거짓() {
        Period p = new Period(LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30));
        assertThat(p.contains(LocalDate.of(2026, 3, 31))).isFalse();
        assertThat(p.contains(LocalDate.of(2026, 5, 1))).isFalse();
    }

    @Test
    void null_파라미터는_NPE() {
        assertThatThrownBy(() -> new Period(null, LocalDate.now()))
                .isInstanceOf(NullPointerException.class);
        assertThatThrownBy(() -> new Period(LocalDate.now(), null))
                .isInstanceOf(NullPointerException.class);
    }
}