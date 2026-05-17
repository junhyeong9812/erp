package com.erp.production.application.dto.command;

public record IssueWorkOrderCommand(Long productId, int plannedQuantity) {}