package com.erp.common.messaging;

import com.erp.common.domain.DomainEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringJUnitConfig
@ContextConfiguration(classes = {SpringEventBus.class, SpringEventBusTest.TestListener.class})
class SpringEventBusTest {

    record HelloEvent(String who, Instant occurredAt) implements DomainEvent {}

    @Component
    static class TestListener {
        final List<HelloEvent> received = new ArrayList<>();
        @EventListener
        public void on(HelloEvent e) { received.add(e); }
    }

    @Autowired SpringEventBus bus;
    @Autowired TestListener listener;

    @Test
    void publish_한_이벤트가_리스너에_전달() {
        bus.publish(new HelloEvent("world", Instant.now()));
        assertThat(listener.received).hasSize(1);
        assertThat(listener.received.get(0).who()).isEqualTo("world");
    }

    @Test
    void publishAll_은_순서대로_여러_이벤트_발행() {
        listener.received.clear();
        bus.publishAll(List.of(
                new HelloEvent("a", Instant.now()),
                new HelloEvent("b", Instant.now()),
                new HelloEvent("c", Instant.now())));

        assertThat(listener.received).extracting(HelloEvent::who)
                .containsExactly("a", "b", "c");
    }
}