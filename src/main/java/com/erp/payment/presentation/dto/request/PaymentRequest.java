package com.erp.payment.presentation.dto.request;

public record PaymentRequest(Long orderId, String method, long amount) {}