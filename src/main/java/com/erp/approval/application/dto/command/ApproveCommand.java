package com.erp.approval.application.dto.command;

import java.util.List;

public record ApproveCommand(Long documentId, Long approverId) {}