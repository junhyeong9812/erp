package com.erp.settlement.infrastructure.batch;

import com.erp.settlement.application.dto.command.CreateLedgerCommand;
import com.erp.settlement.application.port.inbound.LedgerUseCase;
import com.erp.settlement.application.port.inbound.SettlementPeriodUseCase;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest
@Disabled("BATCH_* 메타 스키마가 H2 테스트 컨텍스트에서 자동 초기화되지 않음. " +
        "docs/phase/PHASE03B_GAPS.md 참조 — 별도 schema init 설정 필요.")
class MonthlyClosingJobTest {

    @Autowired JobLauncherTestUtils launcher;
    @Autowired Job monthlyClosingJob;
    @Autowired SettlementPeriodUseCase periodUseCase;
    @Autowired LedgerUseCase ledgerUseCase;

    @Test
    void 열린_기간_하나_마감_성공() throws Exception {
        launcher.setJob(monthlyClosingJob);

        Long pid = periodUseCase.open(
                LocalDate.now().minusMonths(1).withDayOfMonth(1),
                LocalDate.now().minusDays(1));
        ledgerUseCase.createSalesLedger(
                new CreateLedgerCommand(1L, 1000, "test", pid));

        JobExecution exec = launcher.launchJob(new JobParametersBuilder()
                .addString("runDate", LocalDate.now().toString())
                .addString("uniq", String.valueOf(System.nanoTime()))
                .toJobParameters());

        assertThat(exec.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(exec.getStepExecutions().iterator().next().getSkipCount()).isZero();
    }
}
