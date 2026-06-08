package com.erp.notification.domain.entity;

import com.erp.notification.domain.event.NotificationSentEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationTest {

    @Test
    void queue_는_PENDING_상태로_생성() {
        Notification n = Notification.queue(1L, "hi", "body", Notification.Channel.EMAIL);

        assertThat(n.getRecipientId()).isEqualTo(1L);
        assertThat(n.getTitle()).isEqualTo("hi");
        assertThat(n.getBody()).isEqualTo("body");
        assertThat(n.getChannel()).isEqualTo(Notification.Channel.EMAIL);
        assertThat(n.getStatus()).isEqualTo(Notification.Status.PENDING);
    }

    @Test
    void markSent_는_SENT_전이_및_이벤트_등록() {
        Notification n = Notification.queue(1L, "hi", "body", Notification.Channel.EMAIL);
        n.assignId(1L);

        n.markSent();

        assertThat(n.getStatus()).isEqualTo(Notification.Status.SENT);
        assertThat(n.events()).hasAtLeastOneElementOfType(NotificationSentEvent.class);
    }

    @Test
    void NotificationSentEvent_payload_확인() {
        Notification n = Notification.queue(7L, "t", "b", Notification.Channel.SMS);
        n.assignId(100L);

        n.markSent();

        NotificationSentEvent event = (NotificationSentEvent) n.events().stream()
                .filter(e -> e instanceof NotificationSentEvent)
                .findFirst().orElseThrow();
        assertThat(event.notificationId()).isEqualTo(100L);
        assertThat(event.recipientId()).isEqualTo(7L);
        assertThat(event.channel()).isEqualTo("SMS");
    }

    @Test
    void markFailed_는_FAILED_전이이고_이벤트_없음() {
        Notification n = Notification.queue(1L, "hi", "body", Notification.Channel.EMAIL);

        n.markFailed();

        assertThat(n.getStatus()).isEqualTo(Notification.Status.FAILED);
        assertThat(n.events()).noneMatch(e -> e instanceof NotificationSentEvent);
    }

    @Test
    void 네_가지_채널_모두_queue_가능() {
        for (Notification.Channel ch : Notification.Channel.values()) {
            Notification n = Notification.queue(1L, "t", "b", ch);
            assertThat(n.getChannel()).isEqualTo(ch);
        }
    }
}