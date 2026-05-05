package com.erp.settlement.infrastructure.batch.dto;

import com.erp.common.domain.Money;

public record AgingBucketed(String bucket, Money amount) {}
