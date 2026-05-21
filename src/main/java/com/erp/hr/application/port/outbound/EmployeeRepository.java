package com.erp.hr.application.port.outbound;

import com.erp.hr.domain.entity.Employee;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(Long id);
    List<Employee> findActive();
}
