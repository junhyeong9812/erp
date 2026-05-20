package com.erp.hr.application.port.inbound;

import com.erp.hr.application.dto.command.CalculatePayrollCommand;

public interface PayrollUseCase {
    Long calculate(CalculatePayrollCommand command);
}