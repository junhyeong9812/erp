package com.erp.promotion.application.port.inbound;

public interface CouponUseCase {
    Long issue(String code, Long customerId, int rate, long amount, java.time.LocalDate expireOn);
}