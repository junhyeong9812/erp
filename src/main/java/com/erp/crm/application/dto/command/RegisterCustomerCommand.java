package com.erp.crm.application.dto.command;

public record RegisterCustomerCommand(String customerCode, String name, String contact,
                                      Long assignedSalesEmployeeId, long creditLimit) {}