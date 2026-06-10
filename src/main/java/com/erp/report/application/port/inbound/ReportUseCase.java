package com.erp.report.application.port.inbound;

import com.erp.report.application.dto.command.GenerateReportCommand;

public interface ReportUseCase {
    Long generate(GenerateReportCommand command);
}