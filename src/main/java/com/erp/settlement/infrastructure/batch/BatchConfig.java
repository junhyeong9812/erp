package com.erp.settlement.infrastructure.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
    // Spring Boot 3.x + Spring Batch 5 에선 자동 구성이 대부분이므로 별도 빈 선언 최소화.
    // JobRepository, JobLauncher, PlatformTransactionManager 는 auto-config.
}