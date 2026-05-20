package com.erp.hr.application.port.outbound;

import com.erp.hr.domain.entity.Attendance;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository {
    Attendance save(Attendance attendance);
    Optional<Attendance> findById(Long id);
    List<Attendance> findByEmployeeAndMonth(Long employeeId, YearMonth period);  // 급여 산출 입력
}