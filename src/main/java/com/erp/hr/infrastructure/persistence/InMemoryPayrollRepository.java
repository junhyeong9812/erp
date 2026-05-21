package com.erp.hr.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.hr.application.port.outbound.*;
import com.erp.hr.domain.entity.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryPayrollRepository extends InMemoryRepository<Payroll, Long> implements PayrollRepository {
    @Override protected Long extractId(Payroll p) { return p.getId(); }
    @Override public Optional<Payroll> findByEmployeeAndPeriod(Long employeeId, String period) {
        return findAllBy(p -> p.getEmployeeId().equals(employeeId) && p.getPeriod().equals(period))
                .stream().findFirst();
    }
}