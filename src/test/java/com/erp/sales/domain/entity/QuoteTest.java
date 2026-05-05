package com.erp.sales.domain.entity;

import com.erp.common.domain.Money;
import com.erp.sales.domain.event.QuoteExpiredEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QuoteTest {

    @Test
    void issue_로_생성한_견적은_ACTIVE_상태() {
        Quote q = Quote.issue(1L, Money.of(5000), LocalDate.of(2030, 1, 1));

        assertThat(q.getStatus()).isEqualTo(Quote.Status.ACTIVE);
        assertThat(q.getCustomerId()).isEqualTo(1L);
        assertThat(q.getTotalAmount()).isEqualTo(Money.of(5000));
        assertThat(q.getValidUntil()).isEqualTo(LocalDate.of(2030, 1, 1));
    }

    @Test
    void accept_하면_ACCEPTED_상태() {
        Quote q = Quote.issue(1L, Money.of(5000), LocalDate.of(2030, 1, 1));
        q.accept();

        assertThat(q.getStatus()).isEqualTo(Quote.Status.ACCEPTED);
    }

    @Test
    void 이미_수락된_견적은_수락_불가() {
        Quote q = Quote.issue(1L, Money.of(5000), LocalDate.of(2030, 1, 1));
        q.accept();

        assertThatThrownBy(q::accept)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("수락 불가 상태");
    }

    @Test
    void expire_하면_EXPIRED_상태이고_QuoteExpiredEvent_발행() {
        Quote q = Quote.issue(1L, Money.of(5000), LocalDate.of(2026, 1, 1));
        q.assignId(1L);

        q.expire();

        assertThat(q.getStatus()).isEqualTo(Quote.Status.EXPIRED);
        assertThat(q.events()).hasAtLeastOneElementOfType(QuoteExpiredEvent.class);
    }

    @Test
    void expire_는_ACTIVE_가_아니면_무시() {
        Quote q = Quote.issue(1L, Money.of(5000), LocalDate.of(2026, 1, 1));
        q.accept();   // ACCEPTED 상태

        q.expire();   // 조용히 무시

        assertThat(q.getStatus()).isEqualTo(Quote.Status.ACCEPTED);
        assertThat(q.events()).isEmpty();
    }

    @Test
    void isExpired_는_today_가_validUntil_보다_뒤면_true() {
        Quote q = Quote.issue(1L, Money.of(5000), LocalDate.of(2026, 4, 30));

        assertThat(q.isExpired(LocalDate.of(2026, 5, 1))).isTrue();
        assertThat(q.isExpired(LocalDate.of(2026, 4, 30))).isFalse();   // 같은 날은 만료 아님
        assertThat(q.isExpired(LocalDate.of(2026, 4, 29))).isFalse();
    }
}