package com.erp.sales.presentation.api;

import com.erp.sales.application.dto.command.PlaceOrderCommand;
import com.erp.sales.application.dto.query.SalesOrderQuery;
import com.erp.sales.application.port.inbound.SalesOrderUseCase;
import com.erp.sales.presentation.dto.response.SalesOrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sales/orders")
public class SalesOrderController {

    private final SalesOrderUseCase salesOrderUseCase;

    public SalesOrderController(SalesOrderUseCase salesOrderUseCase) {
        this.salesOrderUseCase = salesOrderUseCase;
    }

    @PostMapping
    public ResponseEntity<Long> place(@RequestBody PlaceOrderCommand command) {
        Long id = salesOrderUseCase.placeOrder(command);
        return ResponseEntity.ok(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrderResponse> findById(@PathVariable Long id) {
        SalesOrderQuery q = salesOrderUseCase.findById(id);
        return ResponseEntity.ok(new SalesOrderResponse(
                q.orderId(), q.customerId(), q.totalAmount(), q.status()));
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Long id) {
        salesOrderUseCase.confirm(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        salesOrderUseCase.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
