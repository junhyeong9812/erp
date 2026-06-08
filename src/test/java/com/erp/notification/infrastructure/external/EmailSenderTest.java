package com.erp.notification.infrastructure.external;

import com.erp.notification.domain.entity.Notification;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EmailSenderTest {

    @Test
    void EMAIL_채널은_true_반환() {
        Notification n = Notification.queue(1L, "t", "b", Notification.Channel.EMAIL);

        assertThat(new EmailSender().deliver(n)).isTrue();
    }

    @Test
    void EMAIL_이_아닌_채널은_false() {
        Notification n = Notification.queue(1L, "t", "b", Notification.Channel.SMS);

        assertThat(new EmailSender().deliver(n)).isFalse();
    }
}