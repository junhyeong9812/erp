package com.erp.hr.presentation.dto.request;

import java.time.LocalDate;

public record HireEmployeeRequest(String employeeNumber, String name, Long departmentId,
                                  LocalDate hiredAt, long baseSalary) {}