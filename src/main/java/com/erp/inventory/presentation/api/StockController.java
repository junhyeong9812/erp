package com.erp.inventory.presentation.api;

import com.erp.inventory.application.dto.command.ReceiveStockCommand;
import com.erp.inventory.application.dto.command.ReserveStockCommand;
import com.erp.inventory.application.port.inbound.StockUseCase;
import com.erp.inventory.presentation.dto.request.ReserveStockRequest;
import com.erp.inventory.presentation.dto.response.StockResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory/stocks")
public class StockController {

    private final StockUseCase stockUseCase;

    public StockController(StockUseCase stockUseCase) {
        this.stockUseCase = stockUseCase;
    }

    @PostMapping("/receive")
    public ResponseEntity<Long> receive(@RequestBody ReserveStockRequest req) {
        Long id = stockUseCase.receive(new ReceiveStockCommand(
                req.productId(), req.warehouseId(), req.quantity(), "api"));
        return ResponseEntity.ok(id);
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserve(@RequestBody ReserveStockRequest req) {
        stockUseCase.reserve(new ReserveStockCommand(
                req.productId(), req.warehouseId(), req.quantity(), req.referenceOrderId()));
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<StockResponse> query(
            @RequestParam Long productId,
            @RequestParam Long warehouseId
    ) {
        var q = stockUseCase.query(productId, warehouseId);
        return ResponseEntity.ok(new StockResponse(
                q.productId(), q.warehouseId(), q.total(), q.reserved(), q.available()));
    }
}