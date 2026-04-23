package com.erp.payment.application.dto.command;

public record RequestPaymentCommand(Long orderId, String method, long amount) {}
