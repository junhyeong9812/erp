package com.erp.crm.application.port.outbound;

import com.erp.crm.domain.entity.Customer;

import java.util.Optional;

public interface CustomerRepository {
    Customer save(Customer customer);
    Optional<Customer> findById(Long id);
    Optional<Customer> findByCustomerCode(String code);
}