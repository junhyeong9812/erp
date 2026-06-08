package com.erp.notification.presentation.dto.response;

public record NotificationResponse(Long id, Long recipientId, String channel, String status) {}