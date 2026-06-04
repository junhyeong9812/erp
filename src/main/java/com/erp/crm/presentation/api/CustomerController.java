package com.erp.crm.presentation.api;

import com.erp.crm.application.dto.command.RegisterCustomerCommand;
import com.erp.crm.application.port.inbound.CustomerUseCase;
import com.erp.crm.presentation.dto.request.RegisterCustomerRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/crm/customers")
public class CustomerController {

    private final CustomerUseCase useCase;

    public CustomerController(CustomerUseCase useCase) { this.useCase = useCase; }

    @PostMapping
    public ResponseEntity<Long> register(@RequestBody RegisterCustomerRequest req) {
        return ResponseEntity.ok(useCase.register(new RegisterCustomerCommand(
                req.customerCode(), req.name(), req.contact(),
                req.assignedSalesEmployeeId(), req.creditLimit())));
    }
}