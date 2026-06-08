package com.erp.notification.application.usecase;

import com.erp.common.domain.DomainEvent;
import com.erp.common.messaging.EventBus;
import com.erp.notification.application.dto.command.SendNotificationCommand;
import com.erp.notification.application.port.outbound.NotificationRepository;
import com.erp.notification.application.port.outbound.NotificationSender;
import com.erp.notification.domain.entity.Notification;
import com.erp.notification.domain.event.NotificationSentEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationServiceTest {

    static class FakeRepo implements NotificationRepository {
        final ConcurrentHashMap<Long, Notification> store = new ConcurrentHashMap<>();
        @Override public Notification save(Notification n) { store.put(n.getId(), n); return n; }
        @Override public Optional<Notification> findById(Long id) { return Optional.ofNullable(store.get(id)); }
    }

    static class StubSender implements NotificationSender {
        final boolean result;
        int calls = 0;
        StubSender(boolean result) { this.result = result; }
        @Override public boolean deliver(Notification n) { calls++; return result; }
    }

    static class CapturingBus implements EventBus {
        final List<DomainEvent> published = new ArrayList<>();
        @Override public void publish(DomainEvent e) { published.add(e); }
        @Override public void publishAll(List<? extends DomainEvent> es) { published.addAll(es); }
        @Override public void publishAll(Iterable<? extends DomainEvent> events) {
            events.forEach(published::add);
        }

    }

    FakeRepo repo;
    CapturingBus bus;

    @BeforeEach
    void setUp() { repo = new FakeRepo(); bus = new CapturingBus(); }

    @Test
    void deliver_성공시_SENT_저장하고_이벤트_발행() {
        StubSender sender = new StubSender(true);
        NotificationService svc = new NotificationService(repo, sender, bus);

        Long id = svc.send(new SendNotificationCommand(1L, "t", "b", "EMAIL"));

        assertThat(repo.findById(id)).isPresent()
                .get().extracting(Notification::getStatus).isEqualTo(Notification.Status.SENT);
        assertThat(bus.published).hasAtLeastOneElementOfType(NotificationSentEvent.class);
    }

    @Test
    void deliver_실패시_FAILED_저장하고_이벤트_없음() {
        StubSender sender = new StubSender(false);
        NotificationService svc = new NotificationService(repo, sender, bus);

        Long id = svc.send(new SendNotificationCommand(1L, "t", "b", "EMAIL"));

        assertThat(repo.findById(id)).isPresent()
                .get().extracting(Notification::getStatus).isEqualTo(Notification.Status.FAILED);
        assertThat(bus.published).noneMatch(e -> e instanceof NotificationSentEvent);
    }

    @Test
    void Channel_문자열_파싱_대소문자_구분() {
        StubSender sender = new StubSender(true);
        NotificationService svc = new NotificationService(repo, sender, bus);

        Long id = svc.send(new SendNotificationCommand(1L, "t", "b", "SMS"));

        assertThat(repo.findById(id)).get().extracting(Notification::getChannel)
                .isEqualTo(Notification.Channel.SMS);
    }

    @Test
    void send_는_항상_sender_를_한_번_호출() {
        StubSender sender = new StubSender(true);
        NotificationService svc = new NotificationService(repo, sender, bus);

        svc.send(new SendNotificationCommand(1L, "t", "b", "EMAIL"));
        svc.send(new SendNotificationCommand(2L, "t2", "b2", "SMS"));

        assertThat(sender.calls).isEqualTo(2);
    }
}