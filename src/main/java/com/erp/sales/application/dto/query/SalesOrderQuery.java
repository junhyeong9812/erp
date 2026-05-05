package com.erp.sales.application.dto.query;

public record SalesOrderQuery(
        Long orderId,
        Long customerId,
        Long quoteId,
        long totalAmount,
        String status
) {}
