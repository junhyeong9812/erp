package com.erp.hr.infrastructure.config;

import com.erp.hr.domain.service.AllowanceCalculator;
import com.erp.hr.domain.service.WorkTimeCalculationPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HrDomainServiceConfig {
    @Bean public WorkTimeCalculationPolicy workTimeCalculationPolicy() { return new WorkTimeCalculationPolicy(); }
    @Bean public AllowanceCalculator allowanceCalculator() { return new AllowanceCalculator(); }
}