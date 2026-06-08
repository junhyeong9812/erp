package com.erp.notification.application.usecase;

import com.erp.approval.domain.event.ApprovalRequestedEvent;
import com.erp.notification.application.dto.command.SendNotificationCommand;
import com.erp.notification.application.port.inbound.NotificationUseCase;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@ContextConfiguration(classes = {
        GlobalEventHandler.class,
        GlobalEventHandlerTest.TestConfig.class
})
class GlobalEventHandlerTest {

    /** UseCase 호출을 캡처하는 Fake. Config 에 싱글톤으로 두고 테스트에서 참조. */
    static class CapturingUseCase implements NotificationUseCase {
        final List<SendNotificationCommand> sent = new ArrayList<>();
        @Override public Long send(SendNotificationCommand cmd) { sent.add(cmd); return (long) sent.size(); }
    }

    @Configuration
    static class TestConfig {
        @Bean CapturingUseCase capturingUseCase() { return new CapturingUseCase(); }
        @Bean NotificationUseCase notificationUseCase(CapturingUseCase c) { return c; }
    }

    @Autowired ApplicationEventPublisher publisher;
    @Autowired CapturingUseCase capturing;

    @Test
    void PaymentCompletedEvent_수신시_EMAIL_알림_생성() {
        capturing.sent.clear();

        publisher.publishEvent(new PaymentCompletedEvent(
                1L, 100L, 50_000L, Instant.now()));

        assertThat(capturing.sent).hasSize(1);
        SendNotificationCommand cmd = capturing.sent.get(0);
        assertThat(cmd.recipientId()).isEqualTo(100L);
        assertThat(cmd.title()).isEqualTo("결제 완료");
        assertThat(cmd.channel()).isEqualTo("EMAIL");
        assertThat(cmd.body()).contains("50000");
    }

    @Test
    void ApprovalRequestedEvent_수신시_SYSTEM_알림_생성() {
        capturing.sent.clear();

        publisher.publishEvent(new ApprovalRequestedEvent(
                /* approvalId   */ 1L,
                /* drafterId    */ 7L,
                /* documentType */ "EXPENSE",
                /* occurredAt   */ Instant.now()));

        assertThat(capturing.sent).hasSize(1);
        SendNotificationCommand cmd = capturing.sent.get(0);
        assertThat(cmd.recipientId()).isEqualTo(7L);
        assertThat(cmd.title()).isEqualTo("결재 요청");
        assertThat(cmd.channel()).isEqualTo("SYSTEM");
        assertThat(cmd.body()).contains("EXPENSE");
    }

    @Test
    void 두_이벤트_연속_발행시_각각_한_건씩_호출() {
        capturing.sent.clear();

        publisher.publishEvent(new PaymentCompletedEvent(1L, 100L, 1_000L, Instant.now()));
        publisher.publishEvent(new ApprovalRequestedEvent(2L, 8L, "LEAVE", Instant.now()));

        assertThat(capturing.sent).hasSize(2);
        assertThat(capturing.sent).extracting(SendNotificationCommand::channel)
                .containsExactly("EMAIL", "SYSTEM");
    }

    @Test
    void 관심_없는_이벤트는_무시() {
        capturing.sent.clear();

        // 임의의 다른 이벤트 발행
        publisher.publishEvent(new Object());

        assertThat(capturing.sent).isEmpty();
    }
}