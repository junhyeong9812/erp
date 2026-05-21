package com.erp.hr.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.hr.application.port.outbound.*;
import com.erp.hr.domain.entity.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryEmployeeRepository extends InMemoryRepository<Employee, Long> implements EmployeeRepository {
    @Override protected Long extractId(Employee e) { return e.getId(); }
    @Override public List<Employee> findActive() {
        return findAllBy(e -> e.getStatus() == Employee.Status.ACTIVE);
    }
}
