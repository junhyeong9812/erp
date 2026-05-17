package com.erp.production.presentation.api;

import com.erp.production.application.dto.command.IssueWorkOrderCommand;
import com.erp.production.application.port.inbound.WorkOrderUseCase;
import com.erp.production.presentation.dto.request.IssueWorkOrderRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production/work-orders")
public class WorkOrderController {

    private final WorkOrderUseCase useCase;

    public WorkOrderController(WorkOrderUseCase useCase) { this.useCase = useCase; }

    @PostMapping
    public ResponseEntity<Long> issue(@RequestBody IssueWorkOrderRequest req) {
        return ResponseEntity.ok(useCase.issueWorkOrder(
                new IssueWorkOrderCommand(req.productId(), req.plannedQuantity())));
    }
}