package com.erp.common.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AggregateRootTest {

    /** 테스트용 AggregateRoot 하위 — 실제 엔티티 흉내 */
    static class TestAggregate extends AggregateRoot {
        public void doSomething() {
            register(new NamedEvent("did"));
        }
    }


    static class NamedEvent implements DomainEvent {
        final String name;
        NamedEvent(String n) { this.name = n; }
    }

    @Test
    void register_하면_events_리스트에_쌓인다() {
        TestAggregate a = new TestAggregate();
        a.doSomething();
        a.doSomething();

        assertThat(a.events()).hasSize(2);
    }

    @Test
    void pullEvents_는_리스트를_비운다() {
        TestAggregate a = new TestAggregate();
        a.doSomething();

        var first = a.pullEvents();
        var second = a.pullEvents();

        assertThat(first).hasSize(1);
        assertThat(second).isEmpty();
    }

    @Test
    void pullEvents_반환값은_불변() {
        TestAggregate a = new TestAggregate();
        a.doSomething();

        var pulled = a.pullEvents();

        assertThatThrownBy(() -> pulled.add(new NamedEvent("x")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void events_는_unmodifiable_View() {
        TestAggregate a = new TestAggregate();
        a.doSomething();

        assertThatThrownBy(() -> a.events().add(new NamedEvent("x")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void DomainEvent_default_occurredAt_은_현재시각에_근접() {
        DomainEvent e = new NamedEvent("x");
        var now = java.time.Instant.now();
        assertThat(e.occurredAt()).isBetween(now.minusSeconds(1), now.plusSeconds(1));
    }
}