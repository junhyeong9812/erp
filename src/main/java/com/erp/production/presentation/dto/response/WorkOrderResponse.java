package com.erp.production.presentation.dto.response;

public record WorkOrderResponse(Long id, Long productId, int plannedQuantity, String status) {}