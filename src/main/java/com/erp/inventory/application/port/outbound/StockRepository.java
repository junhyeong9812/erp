package com.erp.inventory.application.port.outbound;

import com.erp.inventory.domain.entity.Stock;

import java.util.List;
import java.util.Optional;

public interface StockRepository {
    Stock save(Stock stock);
    Optional<Stock> findById(Long id);
    Optional<Stock> findByProductAndWarehouse(Long productId, Long warehouseId);
    List<Stock> findAllByProduct(Long productId);
}