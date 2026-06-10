package com.erp.report.application.usecase;

import com.erp.report.application.dto.command.GenerateReportCommand;
import com.erp.report.application.port.inbound.ReportUseCase;
import com.erp.report.application.port.outbound.MetricRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Map;

@Component
public class DailySnapshotScheduler {

    private final ReportUseCase reportUseCase;
    private final MetricRepository metricRepository;

    public DailySnapshotScheduler(ReportUseCase reportUseCase, MetricRepository metricRepository) {
        this.reportUseCase = reportUseCase;
        this.metricRepository = metricRepository;
    }

    @Scheduled(cron = "0 5 0 * * *")  // 매일 00:05
    public void generate() {
        double totalPayment = metricRepository.findByName("payment.amount")
                .stream().mapToDouble(m -> m.getValue()).sum();
        double totalQuantity = metricRepository.findByName("sales.order.quantity")
                .stream().mapToDouble(m -> m.getValue()).sum();

        reportUseCase.generate(new GenerateReportCommand(
                "DAILY_SALES",
                LocalDate.now().minusDays(1),
                Map.of("total_payment", totalPayment, "total_quantity", totalQuantity)
        ));
    }
}