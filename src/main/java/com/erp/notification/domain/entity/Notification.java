package com.erp.notification.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.notification.domain.event.NotificationSentEvent;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "notification")
public class Notification extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long recipientId;
    private String title;
    @Column(length = 2000)
    private String body;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    @Enumerated(EnumType.STRING)
    private Status status;

    protected Notification() {}

    public static Notification queue(Long recipientId, String title, String body, Channel channel) {
        Notification n = new Notification();
        n.recipientId = recipientId;
        n.title = title;
        n.body = body;
        n.channel = channel;
        n.status = Status.PENDING;
        return n;
    }

    public void markSent() {
        this.status = Status.SENT;
        register(new NotificationSentEvent(this.id, this.recipientId, this.channel.name(), Instant.now()));
    }

    public void markFailed() { this.status = Status.FAILED; }

    public void assignId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public Long getRecipientId() { return recipientId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public Channel getChannel() { return channel; }
    public Status getStatus() { return status; }

    public enum Channel { EMAIL, SMS, PUSH, SYSTEM }
    public enum Status { PENDING, SENT, FAILED }
}