package com.erp.promotion.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.promotion.application.port.outbound.CouponRepository;
import com.erp.promotion.domain.entity.Coupon;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryCouponRepository extends InMemoryRepository<Coupon, Long> implements CouponRepository {
    @Override protected Long extractId(Coupon c) { return c.getId(); }
    @Override public Optional<Coupon> findByCode(String code) {
        return findAllBy(c -> c.getCode().equals(code)).stream().findFirst();
    }
}