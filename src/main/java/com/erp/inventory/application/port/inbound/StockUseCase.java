package com.erp.inventory.application.port.inbound;

import com.erp.inventory.application.dto.command.ReserveStockCommand;
import com.erp.inventory.application.dto.command.ReceiveStockCommand;
import com.erp.inventory.application.dto.query.StockQuery;

public interface StockUseCase {
    Long receive(ReceiveStockCommand command);
    void reserve(ReserveStockCommand command);
    void release(Long productId, Long warehouseId, int quantity);
    void ship(Long productId, Long warehouseId, int quantity, Long referenceOrderId);
    StockQuery query(Long productId, Long warehouseId);
}