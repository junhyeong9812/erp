package com.erp.hr.application.port.outbound;

import com.erp.hr.domain.entity.WorkSchedule;
import java.util.Optional;

public interface WorkScheduleRepository {
    WorkSchedule save(WorkSchedule schedule);
    Optional<WorkSchedule> findByEmployee(Long employeeId);
}