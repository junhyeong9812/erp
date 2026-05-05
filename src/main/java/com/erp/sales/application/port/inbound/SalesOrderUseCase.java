package com.erp.sales.application.port.inbound;

import com.erp.sales.application.dto.command.PlaceOrderCommand;

public interface SalesOrderUseCase {
    Long placeOrder(PlaceOrderCommand command);
}
