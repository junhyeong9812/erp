package com.erp.hr.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.hr.application.port.outbound.WorkScheduleRepository;
import com.erp.hr.domain.entity.WorkSchedule;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryWorkScheduleRepository extends InMemoryRepository<WorkSchedule, Long> implements WorkScheduleRepository {
    @Override
    protected Long extractId(WorkSchedule s) {
        return s.getId();
    }

    @Override
    public Optional<WorkSchedule> findByEmployee(Long employeeId) {
        return findAllBy(s -> s.getEmployeeId().equals(employeeId)).stream().findFirst();
    }
}
