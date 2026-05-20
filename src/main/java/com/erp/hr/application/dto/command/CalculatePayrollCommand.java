package com.erp.hr.application.dto.command;

// 기준급여는 Employee에서 읽고, 수당은 근태에서 산출한다.
public record CalculatePayrollCommand(Long employeeId, int year, int month, double insuranceRate) {}