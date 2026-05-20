package com.erp.hr.application.usecase;

import com.erp.common.support.IdGenerator;
import com.erp.hr.application.dto.command.CheckInCommand;
import com.erp.hr.application.dto.command.CheckOutCommand;
import com.erp.hr.application.port.inbound.AttendanceUseCase;
import com.erp.hr.application.port.outbound.AttendanceRepository;
import com.erp.hr.domain.entity.Attendance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AttendanceService implements AttendanceUseCase {

    private final AttendanceRepository repo;

    public AttendanceService(AttendanceRepository repo) {
        this.repo = repo;
    }

    @Override
    public Long checkIn(CheckInCommand cmd) {
        Attendance a = Attendance.checkIn(cmd.employeeId(), cmd.at());
        a.assignId(IdGenerator.next());
        repo.save(a);
        return a.getId();
    }

    @Override
    public void checkOut(CheckOutCommand cmd) {
        Attendance a = repo.findById(cmd.attendanceId()).orElseThrow();
        a.checkOut(cmd.at());
        repo.save(a);
    }
}