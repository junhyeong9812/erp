package com.erp.procurement.application.dto.command;

public record IssuePurchaseOrderCommand(Long supplierId, Long productId, int quantity, long unitPrice) {}

