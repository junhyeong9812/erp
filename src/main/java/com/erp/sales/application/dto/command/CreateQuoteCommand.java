package com.erp.sales.application.dto.command;

import java.time.LocalDate;
import java.util.List;

public record CreateQuoteCommand(Long customerId, List<Line> lines, LocalDate validUntil) {
    public record Line(Long productId, int quantity, long unitPrice) {}
}
