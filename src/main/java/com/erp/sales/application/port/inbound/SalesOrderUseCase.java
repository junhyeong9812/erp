package com.erp.sales.application.port.inbound;

import com.erp.sales.application.dto.command.PlaceOrderCommand;
import com.erp.sales.application.dto.query.SalesOrderQuery;

public interface SalesOrderUseCase {
    Long placeOrder(PlaceOrderCommand command);
    SalesOrderQuery findById(Long orderId);
    void confirm(Long orderId);
    void cancel(Long orderId);
}
