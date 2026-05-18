package com.erp.production.domain.entity;

import com.erp.production.domain.event.ProductionCompletedEvent;
import com.erp.production.domain.event.WorkOrderIssuedEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WorkOrderTest {

    @Test
    void issue_로_생성하면_PLANNED_상태와_Issued_이벤트() {
        WorkOrder wo = WorkOrder.issue(100L, 50);

        assertThat(wo.getProductId()).isEqualTo(100L);
        assertThat(wo.getPlannedQuantity()).isEqualTo(50);
        assertThat(wo.getProducedQuantity()).isZero();
        assertThat(wo.getStatus()).isEqualTo(WorkOrder.Status.PLANNED);
        assertThat(wo.events())
                .hasSize(1)
                .hasAtLeastOneElementOfType(WorkOrderIssuedEvent.class);
    }

    @Test
    void recordProduction_은_생산량과_불량_누적() {
        WorkOrder wo = WorkOrder.issue(100L, 100);
        wo.assignId(1L);

        wo.recordProduction(30, 2);
        wo.recordProduction(20, 1);

        assertThat(wo.getProducedQuantity()).isEqualTo(50);
        // 계획 미달이므로 IN_PROGRESS
        assertThat(wo.getStatus()).isEqualTo(WorkOrder.Status.IN_PROGRESS);
    }

    @Test
    void 계획량_미달이면_IN_PROGRESS_상태() {
        WorkOrder wo = WorkOrder.issue(100L, 50);
        wo.assignId(1L);

        wo.recordProduction(10, 0);

        assertThat(wo.getStatus()).isEqualTo(WorkOrder.Status.IN_PROGRESS);
    }

    @Test
    void 계획량_도달_시_COMPLETED_와_ProductionCompletedEvent_발행() {
        WorkOrder wo = WorkOrder.issue(100L, 50);
        wo.assignId(1L);

        wo.recordProduction(50, 0);

        assertThat(wo.getStatus()).isEqualTo(WorkOrder.Status.COMPLETED);
        // Issued + Completed 총 2건
        assertThat(wo.events())
                .hasSize(2)
                .hasAtLeastOneElementOfType(ProductionCompletedEvent.class);
    }

    @Test
    void 계획량_초과_생산도_완료로_인정() {
        WorkOrder wo = WorkOrder.issue(100L, 50);
        wo.assignId(1L);

        wo.recordProduction(60, 0);

        assertThat(wo.getStatus()).isEqualTo(WorkOrder.Status.COMPLETED);
        assertThat(wo.getProducedQuantity()).isEqualTo(60);
    }

    @Test
    void 분할_생산_후_누적이_계획_도달하면_완료() {
        WorkOrder wo = WorkOrder.issue(100L, 30);
        wo.assignId(1L);

        wo.recordProduction(10, 0);   // IN_PROGRESS
        wo.recordProduction(10, 0);   // IN_PROGRESS
        wo.recordProduction(10, 0);   // COMPLETED

        assertThat(wo.getStatus()).isEqualTo(WorkOrder.Status.COMPLETED);
        assertThat(wo.events())
                .hasAtLeastOneElementOfType(ProductionCompletedEvent.class);
    }
}