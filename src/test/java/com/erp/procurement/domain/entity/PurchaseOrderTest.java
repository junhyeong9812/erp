package com.erp.procurement.domain.entity;

import com.erp.common.domain.Money;
import com.erp.procurement.domain.event.GoodsReceivedEvent;
import com.erp.procurement.domain.event.PurchaseOrderIssuedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PurchaseOrderTest {

    @Test
    void issue_하면_ISSUED_상태이고_PurchaseOrderIssuedEvent_발행() {
        PurchaseOrder po = PurchaseOrder.issue(1L, 100L, 50, Money.of(1000));

        assertThat(po.getStatus()).isEqualTo(PurchaseOrder.Status.ISSUED);
        assertThat(po.getSupplierId()).isEqualTo(1L);
        assertThat(po.getProductId()).isEqualTo(100L);
        assertThat(po.getQuantity()).isEqualTo(50);
        assertThat(po.getReceivedQuantity()).isZero();
        assertThat(po.events()).hasAtLeastOneElementOfType(PurchaseOrderIssuedEvent.class);
    }

    @Test
    void receive_일부_수량이면_PARTIAL_상태() {
        PurchaseOrder po = PurchaseOrder.issue(1L, 100L, 50, Money.of(1000));
        po.assignId(1L);

        po.receive(30);

        assertThat(po.getStatus()).isEqualTo(PurchaseOrder.Status.PARTIAL);
        assertThat(po.getReceivedQuantity()).isEqualTo(30);
    }

    @Test
    void receive_전량이면_COMPLETED_상태() {
        PurchaseOrder po = PurchaseOrder.issue(1L, 100L, 50, Money.of(1000));
        po.assignId(1L);

        po.receive(30);
        po.receive(20);

        assertThat(po.getStatus()).isEqualTo(PurchaseOrder.Status.COMPLETED);
        assertThat(po.getReceivedQuantity()).isEqualTo(50);
    }

    @Test
    void receive_초과_수량이어도_COMPLETED() {
        PurchaseOrder po = PurchaseOrder.issue(1L, 100L, 50, Money.of(1000));
        po.assignId(1L);

        po.receive(60);   // 주문량 초과

        assertThat(po.getStatus()).isEqualTo(PurchaseOrder.Status.COMPLETED);
        assertThat(po.getReceivedQuantity()).isEqualTo(60);
    }

    @Test
    void receive_시_GoodsReceivedEvent_발행() {
        PurchaseOrder po = PurchaseOrder.issue(1L, 100L, 50, Money.of(1000));
        po.assignId(1L);
        po.pullEvents();   // issue 이벤트 먼저 비워두기

        po.receive(10);

        assertThat(po.events()).hasAtLeastOneElementOfType(GoodsReceivedEvent.class);
    }

    @Test
    void COMPLETED_상태에서_receive_재호출은_예외() {
        PurchaseOrder po = PurchaseOrder.issue(1L, 100L, 50, Money.of(1000));
        po.assignId(1L);
        po.receive(50);   // COMPLETED

        assertThatThrownBy(() -> po.receive(1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 완료");
    }
}