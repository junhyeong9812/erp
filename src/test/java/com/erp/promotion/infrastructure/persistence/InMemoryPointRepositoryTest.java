package com.erp.promotion.infrastructure.persistence;

import com.erp.promotion.domain.entity.Point;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryPointRepositoryTest {

    InMemoryPointRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryPointRepository(); }

    @Test
    void findActiveByCustomer_는_ACTIVE_만_반환() {
        Point a = Point.earn(1L, 100, LocalDate.of(2030, 1, 1)); a.assignId(1L);
        Point b = Point.earn(1L, 200, LocalDate.of(2030, 1, 1)); b.assignId(2L); b.consume(200);  // USED
        Point c = Point.earn(2L, 300, LocalDate.of(2030, 1, 1)); c.assignId(3L);                  // 다른 고객
        repo.save(a); repo.save(b); repo.save(c);

        var actives = repo.findActiveByCustomer(1L);

        assertThat(actives).extracting(Point::getId).containsExactly(1L);
    }

    @Test
    void findActiveByCustomer_해당_고객이_없으면_빈_리스트() {
        assertThat(repo.findActiveByCustomer(999L)).isEmpty();
    }
}