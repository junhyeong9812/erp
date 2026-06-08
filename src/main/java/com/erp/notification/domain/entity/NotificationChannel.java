package com.erp.notification.domain.entity;

import com.erp.common.domain.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "notification_channel_pref")
public class NotificationChannel extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private Notification.Channel channel;

    private boolean enabled;

    protected NotificationChannel() {}

    public static NotificationChannel enable(Long userId, Notification.Channel channel) {
        NotificationChannel c = new NotificationChannel();
        c.userId = userId; c.channel = channel; c.enabled = true;
        return c;
    }

    public void disable() { this.enabled = false; }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public boolean isEnabled() { return enabled; }
}