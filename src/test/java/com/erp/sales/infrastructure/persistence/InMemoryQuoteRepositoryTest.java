package com.erp.sales.infrastructure.persistence;

import com.erp.common.domain.Money;
import com.erp.sales.domain.entity.Quote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryQuoteRepositoryTest {

    private InMemoryQuoteRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryQuoteRepository(); }

    @Test
    void save_후_findById() {
        Quote q = Quote.issue(1L, Money.of(5000), LocalDate.of(2030, 1, 1));
        q.assignId(100L);
        repo.save(q);

        assertThat(repo.findById(100L)).isPresent()
                .get().extracting(Quote::getStatus).isEqualTo(Quote.Status.ACTIVE);
    }

    @Test
    void findActiveExpirableQuotes_는_ACTIVE_만() {
        Quote active = Quote.issue(1L, Money.of(1000), LocalDate.of(2030, 1, 1));
        active.assignId(1L);
        Quote accepted = Quote.issue(2L, Money.of(1000), LocalDate.of(2030, 1, 1));
        accepted.assignId(2L);
        accepted.accept();
        Quote expired = Quote.issue(3L, Money.of(1000), LocalDate.of(2026, 1, 1));
        expired.assignId(3L);
        expired.expire();

        repo.save(active);
        repo.save(accepted);
        repo.save(expired);

        assertThat(repo.findActiveExpirableQuotes())
                .extracting(Quote::getId)
                .containsExactly(1L);
    }
}