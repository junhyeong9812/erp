package com.erp.settlement.application.dto.command;

public record CreateLedgerCommand(Long referenceId, long amount, String description, Long periodId) {}
