package com.erp.inventory.presentation.dto.request;

public record ReserveStockRequest(Long productId, Long warehouseId, int quantity, Long referenceOrderId) {}

