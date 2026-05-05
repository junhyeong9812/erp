package com.erp.procurement.application.dto.command;

public record RegisterSupplierQuoteCommand(Long supplierId, Long productId, int quantity, long unitPrice) {}
