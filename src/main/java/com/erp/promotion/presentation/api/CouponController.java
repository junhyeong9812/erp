package com.erp.promotion.presentation.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/promotion/coupons")
class CouponController {
    private final com.erp.promotion.application.port.inbound.CouponUseCase useCase;
    public CouponController(com.erp.promotion.application.port.inbound.CouponUseCase useCase) { this.useCase = useCase; }
}