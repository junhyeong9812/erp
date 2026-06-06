package com.erp.promotion.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.support.IdGenerator;
import com.erp.promotion.application.port.inbound.CouponUseCase;
import com.erp.promotion.application.port.outbound.CouponRepository;
import com.erp.promotion.domain.entity.Coupon;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class CouponService implements CouponUseCase {

    private final CouponRepository repo;

    public CouponService(CouponRepository repo) { this.repo = repo; }

    @Override
    public Long issue(String code, Long customerId, int rate, long amount, LocalDate expireOn) {
        Coupon c = Coupon.issue(code, customerId, rate, Money.of(amount), expireOn);
        c.assignId(IdGenerator.next());
        repo.save(c);
        return c.getId();
    }
}