package com.erp.hr.application.port.outbound;

import com.erp.hr.domain.entity.Payroll;
import java.util.Optional;

public interface PayrollRepository {
    Payroll save(Payroll payroll);
    Optional<Payroll> findById(Long id);
    Optional<Payroll> findByEmployeeAndPeriod(Long employeeId, String period);   // 재계산 가드
}