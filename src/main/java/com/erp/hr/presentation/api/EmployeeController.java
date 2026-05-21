package com.erp.hr.presentation.api;

import com.erp.hr.application.dto.command.HireEmployeeCommand;
import com.erp.hr.application.port.inbound.EmployeeUseCase;
import com.erp.hr.presentation.dto.request.HireEmployeeRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr/employees")
public class EmployeeController {

    private final EmployeeUseCase useCase;

    public EmployeeController(EmployeeUseCase useCase) { this.useCase = useCase; }

    @PostMapping
    public ResponseEntity<Long> hire(@RequestBody HireEmployeeRequest req) {
        return ResponseEntity.ok(useCase.hire(new HireEmployeeCommand(
                req.employeeNumber(), req.name(), req.departmentId(), req.hiredAt(), req.baseSalary())));
    }
}