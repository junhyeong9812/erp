package com.erp.procurement.presentation.api;

import com.erp.procurement.application.dto.command.IssuePurchaseOrderCommand;
import com.erp.procurement.application.port.inbound.PurchaseOrderUseCase;
import com.erp.procurement.presentation.dto.request.IssuePurchaseOrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/procurement/orders")
public class PurchaseOrderController {

    private final PurchaseOrderUseCase useCase;

    public PurchaseOrderController(PurchaseOrderUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<Long> issue(@RequestBody IssuePurchaseOrderRequest req) {
        Long id = useCase.issuePurchaseOrder(new IssuePurchaseOrderCommand(
                req.supplierId(), req.productId(), req.quantity(), req.unitPrice()));
        return ResponseEntity.ok(id);
    }
}