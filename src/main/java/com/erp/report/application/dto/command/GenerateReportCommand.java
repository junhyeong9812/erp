package com.erp.report.application.dto.command;

import java.time.LocalDate;
import java.util.Map;

public record GenerateReportCommand(String reportType, LocalDate targetDate, Map<String, Double> metrics) {}