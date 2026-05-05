package com.erp.procurement.presentation.dto.response;

public record PurchaseOrderResponse(Long id, Long supplierId, Long productId, int quantity, String status) {}

