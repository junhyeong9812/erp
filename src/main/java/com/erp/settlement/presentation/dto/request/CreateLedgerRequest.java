package com.erp.settlement.presentation.dto.request;

public record CreateLedgerRequest(Long referenceId, long amount, String description, Long periodId) {}
