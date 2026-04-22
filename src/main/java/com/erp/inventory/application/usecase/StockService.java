package com.erp.inventory.application.usecase;

import com.erp.common.domain.Quantity;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.inventory.application.dto.command.ReceiveStockCommand;
import com.erp.inventory.application.dto.command.ReserveStockCommand;
import com.erp.inventory.application.dto.query.StockQuery;
import com.erp.inventory.application.port.inbound.StockUseCase;
import com.erp.inventory.application.port.outbound.StockRepository;
import com.erp.inventory.domain.entity.Stock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StockService implements StockUseCase {

    private final StockRepository stockRepository;
    private final EventBus eventBus;

    public StockService(StockRepository stockRepository, EventBus eventBus) {
        this.stockRepository = stockRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Long receive(ReceiveStockCommand cmd) {
        Stock stock = stockRepository.findByProductAndWarehouse(cmd.productId(), cmd.warehouseId())
                .orElseGet(() -> {
                    Stock s = Stock.open(cmd.productId(), cmd.warehouseId(), Quantity.ZERO);
                    s.assignId(IdGenerator.next());
                    return s;
                });
        stock.receive(Quantity.of(cmd.quantity()));
        stockRepository.save(stock);
        eventBus.publishAll(stock.pullEvents());
        return stock.getId();
    }

    @Override
    public void reserve(ReserveStockCommand cmd) {
        Stock stock = stockRepository.findByProductAndWarehouse(cmd.productId(), cmd.warehouseId())
                .orElseThrow(() -> new NotFoundException());
        stock.reserve(Quantity.of(cmd.quantity()));
        stockRepository.save(stock);
        eventBus.publishAll(stock.pullEvents());
    }

    @Override
    public void release(Long productId, Long warehouseId, int quantity) {
        Stock stock = stockRepository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new NotFoundException());
        stock.release(Quantity.of(quantity));
        stockRepository.save(stock);
    }

    @Override
    public void ship(Long productId, Long warehouseId, int quantity, Long referenceOrderId) {
        Stock stock = stockRepository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new NotFoundException());
        stock.ship(Quantity.of(quantity));
        stockRepository.save(stock);
        eventBus.publishAll(stock.pullEvents());
    }

    @Override
    @Transactional(readOnly = true)
    public StockQuery query(Long productId, Long warehouseId) {
        Stock stock = stockRepository.findByProductAndWarehouse(productId, warehouseId)
                .orElseThrow(() -> new NotFoundException());
        return new StockQuery(
                stock.getProductId(),
                stock.getWarehouseId(),
                stock.totalQuantity().value(),
                stock.reservedQuantity().value(),
                stock.availableQuantity().value()
        );
    }
}