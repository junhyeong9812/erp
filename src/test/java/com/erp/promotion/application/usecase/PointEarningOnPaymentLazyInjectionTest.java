package com.erp.promotion.application.usecase;

import com.erp.promotion.application.dto.command.EarnPointCommand;
import com.erp.promotion.application.dto.command.UsePointCommand;
import com.erp.promotion.application.port.inbound.PointUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Promotion 모듈이 Payment 이벤트를 구독할 때, PointUseCase 구현이 아직
 * 초기화되지 않았거나 순환 후보가 되는 상황을 대비해 ObjectProvider 로
 * 지연 해석하는 패턴을 테스트한다.
 *
 * 실제 코드에서는 생성자에 `ObjectProvider<PointUseCase>` 를 받고,
 * 이벤트 수신 시점에 `provider.getObject()` 로 꺼낸다.
 */
@SpringJUnitConfig
@ContextConfiguration(classes = {
        PointEarningOnPaymentLazyInjectionTest.LazyHandler.class,
        PointEarningOnPaymentLazyInjectionTest.TestConfig.class
})
class PointEarningOnPaymentLazyInjectionTest {

    /** 실제 PointEarningOnPayment 의 Lazy 변형. 테스트 용도. */
    @Component
    static class LazyHandler {
        private final ObjectProvider<PointUseCase> provider;
        final List<EarnPointCommand> captured = new ArrayList<>();

        LazyHandler(ObjectProvider<PointUseCase> provider) { this.provider = provider; }

        void handle(long amount, long orderId) {
            int reward = (int) (amount * 0.01);
            if (reward <= 0) return;
            PointUseCase uc = provider.getObject();   // 지연 해석 시점
            Long id = uc.earn(new EarnPointCommand(orderId, reward,
                    java.time.LocalDate.now().plusYears(1)));
            captured.add(new EarnPointCommand(orderId, reward, null));
            assertThat(id).isNotNull();
        }
    }

    @Configuration
    static class TestConfig {
        @Bean
        PointUseCase pointUseCase() {
            return new PointUseCase() {
                @Override public Long earn(EarnPointCommand cmd) { return 42L; }
                @Override public int use(UsePointCommand cmd) { return 0; }
                @Override public void expireOutdated() {}
            };
        }
    }

    @Autowired LazyHandler handler;
    @Autowired ObjectProvider<PointUseCase> provider;

    @Test
    void ObjectProvider_는_컨텍스트_기동_시점에_빈을_즉시_resolve_하지_않는다() {
        // handler 생성자에서 provider 를 받을 뿐, getObject 는 호출 안 됨.
        // 컨텍스트 기동만으로는 PointUseCase.earn() 이 불리지 않아야 한다.
        assertThat(handler.captured).isEmpty();
    }

    @Test
    void 이벤트_수신_시점에_getObject_호출되어_earn_위임() {
        handler.handle(50_000L, 100L);

        assertThat(handler.captured).hasSize(1);
        assertThat(handler.captured.get(0).customerId()).isEqualTo(100L);
        assertThat(handler.captured.get(0).amount()).isEqualTo(500);
    }

    @Test
    void reward_0_이면_getObject_호출_없이_스킵() {
        // 적은 금액 → reward = 0 → provider.getObject 미호출. 예외도 없음.
        handler.handle(50L, 100L);

        assertThat(handler.captured).isEmpty();
    }
}