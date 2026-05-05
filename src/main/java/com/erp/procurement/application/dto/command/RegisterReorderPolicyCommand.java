package com.erp.procurement.application.dto.command;

public record RegisterReorderPolicyCommand(Long productId, Long defaultSupplierId, int reorderQuantity) {}
