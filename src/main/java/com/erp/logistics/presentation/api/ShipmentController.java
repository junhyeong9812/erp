package com.erp.logistics.presentation.api;

import com.erp.logistics.application.dto.command.DispatchShipmentCommand;
import com.erp.logistics.application.port.inbound.ShipmentUseCase;
import com.erp.logistics.presentation.dto.request.DispatchShipmentRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/logistics/shipments")
public class ShipmentController {

    private final ShipmentUseCase useCase;

    public ShipmentController(ShipmentUseCase useCase) { this.useCase = useCase; }

    @PostMapping("/{id}/dispatch")
    public ResponseEntity<Void> dispatch(@PathVariable Long id, @RequestBody DispatchShipmentRequest req) {
        useCase.dispatch(new DispatchShipmentCommand(id, req.driverId(), req.trackingNumber()));
        return ResponseEntity.ok().build();
    }
}