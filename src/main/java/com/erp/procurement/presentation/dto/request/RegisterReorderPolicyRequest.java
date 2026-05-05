package com.erp.procurement.presentation.dto.request;

public record RegisterReorderPolicyRequest(Long productId, Long defaultSupplierId, int reorderQuantity) {}
