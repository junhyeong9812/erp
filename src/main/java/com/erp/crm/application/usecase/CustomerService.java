package com.erp.crm.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.crm.application.dto.command.RegisterCustomerCommand;
import com.erp.crm.application.port.inbound.CustomerUseCase;
import com.erp.crm.application.port.outbound.CustomerRepository;
import com.erp.crm.domain.entity.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CustomerService implements CustomerUseCase {

    private final CustomerRepository repo;
    private final EventBus eventBus;

    public CustomerService(CustomerRepository repo, EventBus eventBus) {
        this.repo = repo;
        this.eventBus = eventBus;
    }

    @Override
    public Long register(RegisterCustomerCommand cmd) {
        Customer c = Customer.register(cmd.customerCode(), cmd.name(), cmd.contact(),
                cmd.assignedSalesEmployeeId(), Money.of(cmd.creditLimit()));
        c.assignId(IdGenerator.next());
        repo.save(c);
        eventBus.publishAll(c.pullEvents());
        return c.getId();
    }

    @Override
    public void recordPurchase(Long customerId, long amount) {
        Customer c = repo.findById(customerId).orElseThrow(NotFoundException::new);
        c.recordPurchase(Money.of(amount));
        repo.save(c);
        eventBus.publishAll(c.pullEvents());
    }
}