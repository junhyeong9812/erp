package com.erp.notification.infrastructure.persistence;

import com.erp.notification.domain.entity.Notification;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryNotificationRepositoryTest {

    @Test
    void save_후_findById() {
        InMemoryNotificationRepository repo = new InMemoryNotificationRepository();
        Notification n = Notification.queue(1L, "t", "b", Notification.Channel.EMAIL);
        n.assignId(1L);

        repo.save(n);

        assertThat(repo.findById(1L)).isPresent()
                .get().extracting(Notification::getTitle).isEqualTo("t");
    }

    @Test
    void 없는_id_는_empty() {
        InMemoryNotificationRepository repo = new InMemoryNotificationRepository();

        assertThat(repo.findById(999L)).isEmpty();
    }
}