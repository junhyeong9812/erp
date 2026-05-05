package com.erp.procurement.presentation.dto.request;

public record IssuePurchaseOrderRequest(Long supplierId, Long productId, int quantity, long unitPrice) {}

