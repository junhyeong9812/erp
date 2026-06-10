package com.erp.report.presentation.dto.request;

import java.time.LocalDate;
import java.util.Map;

public record GenerateReportRequest(String reportType, LocalDate targetDate, Map<String, Double> metrics) {}