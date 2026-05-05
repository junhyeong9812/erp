package com.erp.procurement.presentation.dto.request;

public record RegisterSupplierQuoteRequest(Long supplierId, Long productId, int quantity, long unitPrice) {}
