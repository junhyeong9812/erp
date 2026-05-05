package com.erp.sales.presentation.dto.response;

public record SalesOrderResponse(Long orderId, Long customerId, long totalAmount, String status) {}