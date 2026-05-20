package com.erp.hr.application.port.inbound;

import com.erp.hr.application.dto.command.HireEmployeeCommand;

public interface EmployeeUseCase {
    Long hire(HireEmployeeCommand command);
    void transfer(Long employeeId, Long newDepartmentId);
}