package com.erp.notification.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "notification_log")
public class NotificationLog extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long notificationId;
    private String status;
    private String message;

    protected NotificationLog() {}

    public static NotificationLog of(Long notificationId, String status, String message) {
        NotificationLog l = new NotificationLog();
        l.notificationId = notificationId;
        l.status = status; l.message = message;
        return l;
    }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
}