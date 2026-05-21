package com.erp.hr.presentation.api;

import com.erp.hr.application.dto.command.CheckInCommand;
import com.erp.hr.application.dto.command.CheckOutCommand;
import com.erp.hr.application.port.inbound.AttendanceUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/hr/attendances")
public class AttendanceController {

    private final AttendanceUseCase useCase;

    public AttendanceController(AttendanceUseCase useCase) { this.useCase = useCase; }

    @PostMapping("/check-in")
    public ResponseEntity<Long> checkIn(@RequestParam Long employeeId, @RequestParam LocalDateTime at) {
        return ResponseEntity.ok(useCase.checkIn(new CheckInCommand(employeeId, at)));
    }

    @PostMapping("/{attendanceId}/check-out")
    public ResponseEntity<Void> checkOut(@PathVariable Long attendanceId, @RequestParam LocalDateTime at) {
        useCase.checkOut(new CheckOutCommand(attendanceId, at));
        return ResponseEntity.ok().build();
    }
}