package com.erp.approval.presentation.dto.response;

public record ApprovalResponse(Long id, String documentType, String status, int currentStep) {}