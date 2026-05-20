package com.erp.hr.application.port.inbound;

import com.erp.hr.application.dto.command.CheckInCommand;
import com.erp.hr.application.dto.command.CheckOutCommand;

public interface AttendanceUseCase {
    Long checkIn(CheckInCommand command);
    void checkOut(CheckOutCommand command);
}