package com.erp.approval.application.dto.command;

import java.util.List;

public record DraftApprovalCommand(Long drafterId, String documentType, String title,
                                   List<Long> approverIds) {}
