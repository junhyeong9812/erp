package com.erp.notification.presentation.dto.request;

public record SendNotificationRequest(Long recipientId, String title, String body, String channel) {}