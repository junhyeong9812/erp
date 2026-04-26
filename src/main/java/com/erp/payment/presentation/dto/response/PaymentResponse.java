package com.erp.payment.presentation.dto.response;

public record PaymentResponse(Long paymentId, Long orderId, long amount, String status) {}
