package com.erp.promotion.application.port.outbound;

import com.erp.promotion.domain.entity.Coupon;

import java.util.Optional;

public interface CouponRepository {
    Coupon save(Coupon coupon);
    Optional<Coupon> findByCode(String code);
}