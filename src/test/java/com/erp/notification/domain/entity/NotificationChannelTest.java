package com.erp.notification.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NotificationChannelTest {

    @Test
    void enable_은_enabled_true() {
        NotificationChannel c = NotificationChannel.enable(1L, Notification.Channel.EMAIL);

        assertThat(c.isEnabled()).isTrue();
    }

    @Test
    void disable_호출_후_enabled_false() {
        NotificationChannel c = NotificationChannel.enable(1L, Notification.Channel.PUSH);

        c.disable();

        assertThat(c.isEnabled()).isFalse();
    }
}