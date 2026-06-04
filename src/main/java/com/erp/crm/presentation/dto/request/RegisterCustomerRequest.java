package com.erp.crm.presentation.dto.request;

public record RegisterCustomerRequest(String customerCode, String name, String contact,
                                      Long assignedSalesEmployeeId, long creditLimit) {}