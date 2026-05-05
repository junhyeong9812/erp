package com.erp.settlement.infrastructure.batch;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BatchContextSmokeTest {

    @Autowired JobRepository jobRepository;
    @Autowired JobLauncher jobLauncher;

    @Autowired @Qualifier("monthlyClosingJob") Job monthlyClosingJob;
    @Autowired @Qualifier("sellerSettlementJob") Job sellerSettlementJob;
    @Autowired @Qualifier("reconciliationJob") Job reconciliationJob;
    @Autowired @Qualifier("agingJob") Job agingJob;
    @Autowired @Qualifier("ledgerCarryOverJob") Job ledgerCarryOverJob;

    @Test
    void 모든_Job_빈이_등록되어_있다() {
        assertThat(jobRepository).isNotNull();
        assertThat(jobLauncher).isNotNull();
        assertThat(monthlyClosingJob.getName()).isEqualTo("monthlyClosingJob");
        assertThat(sellerSettlementJob.getName()).isEqualTo("sellerSettlementJob");
        assertThat(reconciliationJob.getName()).isEqualTo("reconciliationJob");
        assertThat(agingJob.getName()).isEqualTo("agingJob");
        assertThat(ledgerCarryOverJob.getName()).isEqualTo("ledgerCarryOverJob");
    }
}
