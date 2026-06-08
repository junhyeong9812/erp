package com.erp.notification.application.dto.command;

public record SendNotificationCommand(Long recipientId, String title, String body, String channel) {}