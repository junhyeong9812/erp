package com.erp.promotion.application.usecase;

import com.erp.promotion.application.port.outbound.CouponRepository;
import com.erp.promotion.domain.entity.Coupon;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class CouponServiceTest {

    static class FakeCouponRepo implements CouponRepository {
        final ConcurrentHashMap<Long, Coupon> byId = new ConcurrentHashMap<>();
        final ConcurrentHashMap<String, Coupon> byCode = new ConcurrentHashMap<>();
        @Override public Coupon save(Coupon c) { byId.put(c.getId(), c); byCode.put(c.getCode(), c); return c; }
        @Override public Optional<Coupon> findByCode(String code) { return Optional.ofNullable(byCode.get(code)); }
    }

    @Test
    void 쿠폰_발급시_id_할당_및_저장() {
        FakeCouponRepo repo = new FakeCouponRepo();
        CouponService service = new CouponService(repo);

        Long id = service.issue("PROMO10", 1L, 10, 0, LocalDate.of(2030, 1, 1));

        assertThat(id).isNotNull();
        assertThat(repo.findByCode("PROMO10")).isPresent()
                .get().extracting(Coupon::getStatus).isEqualTo(Coupon.Status.ISSUED);
    }
}