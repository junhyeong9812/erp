package com.erp.inventory.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.inventory.application.port.outbound.StockRepository;
import com.erp.inventory.domain.entity.Stock;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryStockRepository
        extends InMemoryRepository<Stock, Long>
        implements StockRepository {

    @Override
    protected Long extractId(Stock entity) {
        return entity.getId();
    }

    @Override
    public Optional<Stock> findByProductAndWarehouse(Long productId, Long warehouseId) {
        return store.values().stream()
                .filter(s -> s.getProductId().equals(productId) && s.getWarehouseId().equals(warehouseId))
                .findFirst();
    }

    @Override
    public List<Stock> findAllByProduct(Long productId) {
        return findAllBy(s -> s.getProductId().equals(productId));
    }
}