package com.erp.crm.application.port.inbound;

import com.erp.crm.application.dto.command.RegisterCustomerCommand;

public interface CustomerUseCase {
    Long register(RegisterCustomerCommand command);
    void recordPurchase(Long customerId, long amount);
}