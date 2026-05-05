package com.erp.procurement.presentation.api;

import com.erp.procurement.application.dto.command.RegisterSupplierQuoteCommand;
import com.erp.procurement.application.port.inbound.SupplierQuoteUseCase;
import com.erp.procurement.presentation.dto.request.RegisterSupplierQuoteRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/procurement/supplier-quotes")
public class SupplierQuoteController {

    private final SupplierQuoteUseCase useCase;

    public SupplierQuoteController(SupplierQuoteUseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    public ResponseEntity<Long> register(@RequestBody RegisterSupplierQuoteRequest req) {
        Long id = useCase.registerSupplierQuote(new RegisterSupplierQuoteCommand(
                req.supplierId(), req.productId(), req.quantity(), req.unitPrice()));
        return ResponseEntity.ok(id);
    }
}
