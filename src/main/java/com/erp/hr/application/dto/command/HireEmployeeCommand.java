package com.erp.hr.application.dto.command;

import java.time.LocalDate;

public record HireEmployeeCommand(String employeeNumber, String name, Long departmentId,
                                  LocalDate hiredAt, long baseSalary) {}