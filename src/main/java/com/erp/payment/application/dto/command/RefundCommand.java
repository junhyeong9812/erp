package com.erp.payment.application.dto.command;

public record RefundCommand(Long paymentId, Long orderId, long amount, String reason) {}