package com.erp.promotion.presentation.dto.request;

public record EarnPointRequest(Long customerId, int amount, String expireOn) {}