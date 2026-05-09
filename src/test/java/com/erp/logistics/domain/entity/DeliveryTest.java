package com.erp.logistics.domain.entity;

import com.erp.logistics.domain.event.DeliveryCompletedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeliveryTest {

    @Test
    void assign_으로_생성하면_ASSIGNED_상태() {
        Delivery d = Delivery.assign(100L, "driver-1", "TRK-001");

        assertThat(d.getShipmentId()).isEqualTo(100L);
        assertThat(d.getStatus()).isEqualTo(Delivery.Status.ASSIGNED);
    }

    @Test
    void start_하면_IN_TRANSIT_상태() {
        Delivery d = Delivery.assign(100L, "driver-1", "TRK-001");
        d.assignId(1L);

        d.start();

        assertThat(d.getStatus()).isEqualTo(Delivery.Status.IN_TRANSIT);
    }

    @Test
    void deliver_하면_DELIVERED_상태() {
        Delivery d = Delivery.assign(100L, "driver-1", "TRK-001");
        d.assignId(1L);
        d.start();

        d.deliver();

        assertThat(d.getStatus()).isEqualTo(Delivery.Status.DELIVERED);
    }

    @Test
    void deliver_시_DeliveryCompletedEvent_발행() {
        Delivery d = Delivery.assign(100L, "driver-1", "TRK-001");
        d.assignId(1L);

        d.deliver();

        assertThat(d.events())
                .hasSize(1)
                .hasAtLeastOneElementOfType(DeliveryCompletedEvent.class);
    }

    @Test
    void deliver_후_pullEvents_는_리스트를_비운다() {
        Delivery d = Delivery.assign(100L, "driver-1", "TRK-001");
        d.assignId(1L);
        d.deliver();

        var pulled = d.pullEvents();

        assertThat(pulled).hasSize(1);
        assertThat(d.events()).isEmpty();
    }
}