package com.erp.hr.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.hr.application.port.outbound.*;
import com.erp.hr.domain.entity.*;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryAttendanceRepository extends InMemoryRepository<Attendance, Long> implements AttendanceRepository {
    @Override protected Long extractId(Attendance a) { return a.getId(); }
    @Override public List<Attendance> findByEmployeeAndMonth(Long employeeId, YearMonth period) {
        return findAllBy(a -> a.getEmployeeId().equals(employeeId)
                && a.getCheckIn() != null
                && YearMonth.from(a.getCheckIn()).equals(period));
    }
}