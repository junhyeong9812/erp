package com.erp.procurement.application.dto.command;

public record ReceiveGoodsCommand(Long purchaseOrderId, int quantity) {}

