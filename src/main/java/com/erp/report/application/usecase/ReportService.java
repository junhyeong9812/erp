package com.erp.report.application.usecase;

import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.report.application.dto.command.GenerateReportCommand;
import com.erp.report.application.port.inbound.ReportUseCase;
import com.erp.report.application.port.outbound.ReportSnapshotRepository;
import com.erp.report.domain.entity.ReportSnapshot;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReportService implements ReportUseCase {

    private final ReportSnapshotRepository repo;
    private final EventBus eventBus;

    public ReportService(ReportSnapshotRepository repo, EventBus eventBus) {
        this.repo = repo;
        this.eventBus = eventBus;
    }

    @Override
    public Long generate(GenerateReportCommand cmd) {
        ReportSnapshot s = ReportSnapshot.generate(cmd.reportType(), cmd.targetDate(), cmd.metrics());
        s.assignId(IdGenerator.next());
        repo.save(s);
        eventBus.publishAll(s.pullEvents());
        return s.getId();
    }
}