package com.erp.auth.application.dto.command;

public record AssignRoleCommand(Long userId, String roleCode) {}