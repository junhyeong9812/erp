package com.erp.logistics.domain.entity;

import com.erp.logistics.domain.event.ShipmentDispatchedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ShipmentTest {

    @Test
    void instruct_로_생성하면_PREPARING_상태() {
        Shipment s = Shipment.instruct(1L, 10L);

        assertThat(s.getOrderId()).isEqualTo(1L);
        assertThat(s.getStatus()).isEqualTo(Shipment.Status.PREPARING);
        assertThat(s.events()).isEmpty();
    }

    @Test
    void dispatch_하면_DISPATCHED_상태로_전이() {
        Shipment s = Shipment.instruct(1L, 10L);
        s.assignId(100L);

        s.dispatch();

        assertThat(s.getStatus()).isEqualTo(Shipment.Status.DISPATCHED);
    }

    @Test
    void dispatch_하면_ShipmentDispatchedEvent_발행() {
        Shipment s = Shipment.instruct(1L, 10L);
        s.assignId(100L);

        s.dispatch();

        assertThat(s.events())
                .hasSize(1)
                .hasAtLeastOneElementOfType(ShipmentDispatchedEvent.class);
    }

    @Test
    void PREPARING_이_아니면_dispatch_불가() {
        Shipment s = Shipment.instruct(1L, 10L);
        s.assignId(100L);
        s.dispatch();  // PREPARING → DISPATCHED

        assertThatThrownBy(s::dispatch)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void complete_호출하면_COMPLETED_상태() {
        Shipment s = Shipment.instruct(1L, 10L);
        s.assignId(100L);

        s.complete();

        assertThat(s.getStatus()).isEqualTo(Shipment.Status.COMPLETED);
    }
}