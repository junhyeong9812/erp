package com.erp.inventory.presentation.dto.response;

public record StockResponse(Long productId, Long warehouseId, int total, int reserved, int available) {}

