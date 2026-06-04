package com.erp.crm.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.crm.application.port.outbound.CustomerRepository;
import com.erp.crm.domain.entity.Customer;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryCustomerRepository extends InMemoryRepository<Customer, Long> implements CustomerRepository {
    @Override protected Long extractId(Customer c) { return c.getId(); }
    @Override public Optional<Customer> findByCustomerCode(String code) {
        return findAllBy(c -> c.getCustomerCode().equals(code)).stream().findFirst();
    }
}