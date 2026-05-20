package com.erp.hr.application.dto.command;

import java.time.LocalDateTime;

public record CheckOutCommand(Long attendanceId, LocalDateTime at) {}