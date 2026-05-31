package com.erp.approval.presentation.dto.request;

import java.util.List;
public record DraftApprovalRequest(Long drafterId, String documentType, String title,
                                   List<Long> approverIds) {}