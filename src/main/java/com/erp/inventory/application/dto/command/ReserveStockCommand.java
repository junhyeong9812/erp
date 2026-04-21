package com.erp.inventory.application.dto.command;

public record ReserveStockCommand(
        Long productId,
        Long warehouseId,
        int quantity,
        Long referenceOrderId) {}