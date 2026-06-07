package com.erp.promotion.domain.entity;

import com.erp.promotion.domain.event.PointEarnedEvent;
import com.erp.promotion.domain.event.PointExpiredEvent;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class PointTest {

    @Test
    void earn_은_ACTIVE_상태로_생성() {
        Point p = Point.earn(1L, 1000, LocalDate.of(2030, 1, 1));

        assertThat(p.getCustomerId()).isEqualTo(1L);
        assertThat(p.getAmount()).isEqualTo(1000);
        assertThat(p.getStatus()).isEqualTo(Point.Status.ACTIVE);
    }

    @Test
    void earn_시_PointEarnedEvent_등록() {
        Point p = Point.earn(1L, 1000, LocalDate.of(2030, 1, 1));

        assertThat(p.events()).hasAtLeastOneElementOfType(PointEarnedEvent.class);
    }

    @Test
    void 부분_사용시_남은_포인트_계산() {
        Point p = Point.earn(1L, 1000, LocalDate.of(2030, 1, 1));

        int used = p.consume(300);

        assertThat(used).isEqualTo(300);
        assertThat(p.getAmount()).isEqualTo(700);
        assertThat(p.getStatus()).isEqualTo(Point.Status.ACTIVE);
    }

    @Test
    void 요청량이_잔액보다_많으면_잔액만큼만_차감() {
        Point p = Point.earn(1L, 500, LocalDate.of(2030, 1, 1));

        int used = p.consume(1000);

        assertThat(used).isEqualTo(500);
        assertThat(p.getAmount()).isZero();
        assertThat(p.getStatus()).isEqualTo(Point.Status.USED);
    }

    @Test
    void 전량_사용시_USED_전이() {
        Point p = Point.earn(1L, 1000, LocalDate.of(2030, 1, 1));
        p.consume(1000);

        assertThat(p.getStatus()).isEqualTo(Point.Status.USED);
    }

    @Test
    void ACTIVE_가_아니면_consume_은_0_반환() {
        Point p = Point.earn(1L, 1000, LocalDate.of(2030, 1, 1));
        p.expire();

        int used = p.consume(100);

        assertThat(used).isZero();
    }

    @Test
    void expire_호출시_EXPIRED_전이_및_이벤트() {
        Point p = Point.earn(1L, 1000, LocalDate.of(2026, 1, 1));
        p.pullEvents();  // 적립 이벤트 비움

        p.expire();

        assertThat(p.getStatus()).isEqualTo(Point.Status.EXPIRED);
        assertThat(p.getAmount()).isZero();
        assertThat(p.events()).hasAtLeastOneElementOfType(PointExpiredEvent.class);
    }

    @Test
    void 이미_EXPIRED_상태에서_expire_재호출은_무시() {
        Point p = Point.earn(1L, 1000, LocalDate.of(2026, 1, 1));
        p.expire();
        p.pullEvents();

        p.expire();

        assertThat(p.events()).noneMatch(e -> e instanceof PointExpiredEvent);
    }

    @Test
    void isExpirable_은_만료일_이후에만_참() {
        Point p = Point.earn(1L, 1000, LocalDate.of(2026, 5, 1));

        assertThat(p.isExpirable(LocalDate.of(2026, 4, 30))).isFalse();
        assertThat(p.isExpirable(LocalDate.of(2026, 5, 1))).isTrue();   // 경계 포함
        assertThat(p.isExpirable(LocalDate.of(2026, 5, 2))).isTrue();
    }

    @Test
    void USED_상태에서는_isExpirable_false() {
        Point p = Point.earn(1L, 100, LocalDate.of(2025, 1, 1));
        p.consume(100);

        assertThat(p.isExpirable(LocalDate.of(2030, 1, 1))).isFalse();
    }
}