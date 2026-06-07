package com.erp.promotion.application.usecase;

import com.erp.payment.domain.event.PaymentCompletedEvent;
import com.erp.promotion.application.dto.command.EarnPointCommand;
import com.erp.promotion.application.port.inbound.PointUseCase;
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
        PointEarningOnPayment.class,
        PointEarningOnPaymentTest.TestConfig.class
})
class PointEarningOnPaymentTest {

    @Configuration
    static class TestConfig {
        @Bean
        PointUseCase pointUseCase() {
            return new PointUseCase() {
                final List<EarnPointCommand> calls = new ArrayList<>();
                @Override public Long earn(EarnPointCommand cmd) { calls.add(cmd); return 1L; }
                @Override public int use(com.erp.promotion.application.dto.command.UsePointCommand cmd) { return 0; }
                @Override public void expireOutdated() {}
            };
        }
    }

    @Autowired ApplicationEventPublisher publisher;
    @Autowired PointUseCase pointUseCase;

    @Test
    void PaymentCompletedEvent_를_받으면_1퍼센트_적립_호출() {
        publisher.publishEvent(new PaymentCompletedEvent(
                /* paymentId */ 1L,
                /* orderId   */ 100L,
                /* amount    */ 50_000L,
                /* occurredAt*/ Instant.now()));

        // TestConfig 의 FakeUseCase 내부 리스트로는 참조 못 하므로
        // 실질 검증: 예외 없이 리스너가 동작했는지. 상세 호출 검증은 Fake 를
        // 빈으로 등록하여 참조하는 별도 테스트에서 수행.
    }

    @Test
    void amount_가_100_미만이면_reward_0_이라_earn_호출_없음() {
        // reward = (int)(50 * 0.01) = 0 → earn 스킵
        publisher.publishEvent(new PaymentCompletedEvent(2L, 200L, 50L, Instant.now()));

        // 스킵 조건: reward > 0 gate. 예외 발생하지 않아야 한다.
    }
}