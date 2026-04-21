package com.erp.inventory.application.dto.command;

public record ReceiveStockCommand(
        Long productId,
        Long warehouseId,
        int quantity,
        String reason) {}
