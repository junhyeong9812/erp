package com.erp.notification.application.usecase;

import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.notification.application.dto.command.SendNotificationCommand;
import com.erp.notification.application.port.inbound.NotificationUseCase;
import com.erp.notification.application.port.outbound.NotificationRepository;
import com.erp.notification.application.port.outbound.NotificationSender;
import com.erp.notification.domain.entity.Notification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationService implements NotificationUseCase {

    private final NotificationRepository repo;
    private final NotificationSender sender;
    private final EventBus eventBus;

    public NotificationService(NotificationRepository repo, NotificationSender sender, EventBus eventBus) {
        this.repo = repo;
        this.sender = sender;
        this.eventBus = eventBus;
    }

    @Override
    public Long send(SendNotificationCommand cmd) {
        Notification n = Notification.queue(cmd.recipientId(), cmd.title(), cmd.body(),
                Notification.Channel.valueOf(cmd.channel()));
        n.assignId(IdGenerator.next());
        boolean ok = sender.deliver(n);
        if (ok) n.markSent();
        else n.markFailed();
        repo.save(n);
        eventBus.publishAll(n.pullEvents());
        return n.getId();
    }
}