package com.erp.crm.domain.entity;

import com.erp.common.domain.Money;
import com.erp.crm.domain.event.CustomerGradeChangedEvent;
import com.erp.crm.domain.event.CustomerRegisteredEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerTest {

    @Test
    void register_로_생성한_Customer_는_NORMAL_등급_CustomerRegisteredEvent_발행() {
        Customer c = Customer.register("C001", "ACME", "01012345678", 1L, Money.of(1_000_000));

        assertThat(c.getCustomerCode()).isEqualTo("C001");
        assertThat(c.getName()).isEqualTo("ACME");
        assertThat(c.getGrade()).isEqualTo(Customer.Grade.NORMAL);
        assertThat(c.getTotalPurchase()).isEqualTo(Money.of(0));
        assertThat(c.events()).hasAtLeastOneElementOfType(CustomerRegisteredEvent.class);
    }

    @Test
    void 총_매출_100만원_미만은_NORMAL_유지() {
        Customer c = Customer.register("C001", "ACME", "-", 1L, Money.of(5_000_000));
        c.assignId(1L);
        c.pullEvents(); // 등록 이벤트 클리어

        c.recordPurchase(Money.of(999_999));

        assertThat(c.getGrade()).isEqualTo(Customer.Grade.NORMAL);
        assertThat(c.events()).noneMatch(e -> e instanceof CustomerGradeChangedEvent);
    }

    @Test
    void 총_매출_100만원_이상은_SILVER_로_전환() {
        Customer c = Customer.register("C001", "ACME", "-", 1L, Money.of(5_000_000));
        c.assignId(1L);
        c.pullEvents();

        c.recordPurchase(Money.of(1_000_000));

        assertThat(c.getGrade()).isEqualTo(Customer.Grade.SILVER);
        assertThat(c.events()).hasAtLeastOneElementOfType(CustomerGradeChangedEvent.class);
    }

    @Test
    void 총_매출_500만원_이상은_GOLD_로_전환() {
        Customer c = Customer.register("C001", "ACME", "-", 1L, Money.of(5_000_000));
        c.assignId(1L);
        c.pullEvents();

        c.recordPurchase(Money.of(5_000_000));

        assertThat(c.getGrade()).isEqualTo(Customer.Grade.GOLD);
    }

    @Test
    void 총_매출_1000만원_이상은_VIP_로_전환() {
        Customer c = Customer.register("C001", "ACME", "-", 1L, Money.of(20_000_000));
        c.assignId(1L);
        c.pullEvents();

        c.recordPurchase(Money.of(10_000_000));

        assertThat(c.getGrade()).isEqualTo(Customer.Grade.VIP);
    }

    @Test
    void 등급_동일_구간_내_추가_매출은_전환_이벤트_미발행() {
        Customer c = Customer.register("C001", "ACME", "-", 1L, Money.of(20_000_000));
        c.assignId(1L);
        c.recordPurchase(Money.of(1_500_000)); // SILVER
        c.pullEvents();

        c.recordPurchase(Money.of(500_000));   // 2,000,000 → 여전히 SILVER

        assertThat(c.getGrade()).isEqualTo(Customer.Grade.SILVER);
        assertThat(c.events()).noneMatch(e -> e instanceof CustomerGradeChangedEvent);
    }

    @Test
    void CustomerGradeChangedEvent_는_old_와_new_를_담는다() {
        Customer c = Customer.register("C001", "ACME", "-", 1L, Money.of(20_000_000));
        c.assignId(7L);
        c.pullEvents();

        c.recordPurchase(Money.of(6_000_000)); // NORMAL → GOLD

        var changed = c.events().stream()
                .filter(e -> e instanceof CustomerGradeChangedEvent)
                .map(e -> (CustomerGradeChangedEvent) e)
                .findFirst()
                .orElseThrow();

        assertThat(changed.customerId()).isEqualTo(7L);
        assertThat(changed.oldGrade()).isEqualTo("NORMAL");
        assertThat(changed.newGrade()).isEqualTo("GOLD");
    }

    @Test
    void canCharge_는_신용한도_이하일_때_true() {
        Customer c = Customer.register("C001", "ACME", "-", 1L, Money.of(1_000_000));

        assertThat(c.canCharge(Money.of(1_000_000))).isTrue();  // 경계 포함
        assertThat(c.canCharge(Money.of(500_000))).isTrue();
        assertThat(c.canCharge(Money.of(1_000_001))).isFalse();
    }

    @Test
    void getTotalPurchase_는_누적합계를_Money_로_반환() {
        Customer c = Customer.register("C001", "ACME", "-", 1L, Money.of(20_000_000));
        c.recordPurchase(Money.of(300_000));
        c.recordPurchase(Money.of(200_000));

        assertThat(c.getTotalPurchase()).isEqualTo(Money.of(500_000));
    }
}