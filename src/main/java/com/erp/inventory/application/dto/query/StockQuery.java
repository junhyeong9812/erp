package com.erp.inventory.application.dto.query;

public record StockQuery(Long productId,
                         Long warehouseId,
                         int total,
                         int reserved,
                         int available) {}