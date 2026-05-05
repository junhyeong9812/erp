package com.erp.sales.presentation.dto.request;

import java.time.LocalDate;
import java.util.List;

public record CreateQuoteRequest(Long customerId, List<Line> lines, LocalDate validUntil) {
    public record Line(Long productId, int quantity, long unitPrice) {}
}