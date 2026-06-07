package com.erp.promotion.infrastructure.persistence;

import com.erp.common.domain.Money;
import com.erp.promotion.domain.entity.Coupon;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryCouponRepositoryTest {

    @Test
    void findByCode_로_조회() {
        InMemoryCouponRepository repo = new InMemoryCouponRepository();
        Coupon c = Coupon.issue("WELCOME", 1L, 10, Money.ZERO, LocalDate.of(2030, 1, 1));
        c.assignId(1L);
        repo.save(c);

        assertThat(repo.findByCode("WELCOME")).isPresent()
                .get().extracting(Coupon::getId).isEqualTo(1L);
    }

    @Test
    void findByCode_없는_코드는_empty() {
        InMemoryCouponRepository repo = new InMemoryCouponRepository();

        assertThat(repo.findByCode("NOPE")).isEmpty();
    }
}