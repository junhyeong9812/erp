package com.erp.sales.application.dto.command;
import java.time.LocalDate;
import java.util.List;

public record PlaceOrderCommand(Long customerId, Long quoteId, List<Line> lines) {
    public record Line(Long productId, int quantity, long unitPrice) {}
}