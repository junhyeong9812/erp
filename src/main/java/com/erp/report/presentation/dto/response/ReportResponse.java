package com.erp.report.presentation.dto.response;

import java.util.Map;

public record ReportResponse(Long id, String reportType, Map<String, Double> metrics) {}