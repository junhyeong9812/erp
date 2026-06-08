package com.erp.notification.infrastructure.external;

import com.erp.notification.application.port.outbound.NotificationSender;
import com.erp.notification.domain.entity.Notification;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompositeSenderTest {

    @Test
    void 채널에_맞는_Sender_가_처리하면_true() {
        CompositeSender composite = new CompositeSender(List.of(new EmailSender(), new SmsSender()));
        Notification email = Notification.queue(1L, "t", "b", Notification.Channel.EMAIL);
        Notification sms = Notification.queue(1L, "t", "b", Notification.Channel.SMS);

        assertThat(composite.deliver(email)).isTrue();
        assertThat(composite.deliver(sms)).isTrue();
    }

    @Test
    void 아무도_처리하지_못하면_false() {
        CompositeSender composite = new CompositeSender(List.of(new EmailSender(), new SmsSender()));
        Notification push = Notification.queue(1L, "t", "b", Notification.Channel.PUSH);

        assertThat(composite.deliver(push)).isFalse();
    }

    @Test
    void 자기자신은_재귀_방지로_제외() {
        CompositeSender inner = new CompositeSender(List.of(new EmailSender()));
        CompositeSender outer = new CompositeSender(List.of(inner, new SmsSender()));
        Notification sms = Notification.queue(1L, "t", "b", Notification.Channel.SMS);

        // outer 가 자기 타입을 필터링하고 나면 SmsSender 만 남아 정상 처리
        assertThat(outer.deliver(sms)).isTrue();
    }

    @Test
    void 빈_Sender_목록이면_항상_false() {
        CompositeSender composite = new CompositeSender(List.of());
        Notification n = Notification.queue(1L, "t", "b", Notification.Channel.EMAIL);

        assertThat(composite.deliver(n)).isFalse();
    }

    @Test
    void 첫번째로_성공한_Sender_에서_종료() {
        // 같은 EMAIL 채널을 처리하는 두 Sender 를 넣어도
        // 첫 번째에서 true 를 받으면 두 번째는 호출되지 않음.
        class Counting implements NotificationSender {
            int calls = 0;
            @Override public boolean deliver(Notification n) { calls++; return false; }
        }
        Counting c1 = new Counting();
        CompositeSender composite = new CompositeSender(List.of(new EmailSender(), c1));
        Notification email = Notification.queue(1L, "t", "b", Notification.Channel.EMAIL);

        composite.deliver(email);

        assertThat(c1.calls).isZero();
    }
}