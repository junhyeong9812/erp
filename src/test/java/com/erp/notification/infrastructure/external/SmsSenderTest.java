package com.erp.notification.infrastructure.external;

import com.erp.notification.domain.entity.Notification;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SmsSenderTest {

    @Test
    void SMS_채널은_true() {
        Notification n = Notification.queue(1L, "t", "b", Notification.Channel.SMS);

        assertThat(new SmsSender().deliver(n)).isTrue();
    }

    @Test
    void SMS_가_아닌_채널은_false() {
        Notification n = Notification.queue(1L, "t", "b", Notification.Channel.PUSH);

        assertThat(new SmsSender().deliver(n)).isFalse();
    }
}