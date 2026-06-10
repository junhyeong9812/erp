package com.erp.report.presentation.api;

import com.erp.report.application.dto.command.GenerateReportCommand;
import com.erp.report.application.port.inbound.ReportUseCase;
import com.erp.report.presentation.dto.request.GenerateReportRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportUseCase useCase;

    public ReportController(ReportUseCase useCase) { this.useCase = useCase; }

    @PostMapping
    public ResponseEntity<Long> generate(@RequestBody GenerateReportRequest req) {
        return ResponseEntity.ok(useCase.generate(new GenerateReportCommand(
                req.reportType(), req.targetDate(), req.metrics())));
    }
}