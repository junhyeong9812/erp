package com.erp.hr.application.dto.command;

import java.time.LocalDateTime;

public record CheckInCommand(Long employeeId, LocalDateTime at) {}
