package com.erp.approval.domain.entity;

import com.erp.approval.domain.event.ApprovalCompletedEvent;
import com.erp.approval.domain.event.ApprovalRequestedEvent;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApprovalDocumentTest {

    @Test
    void draft_는_IN_PROGRESS_로_생성되고_currentStep_0() {
        ApprovalDocument d = ApprovalDocument.draft(1L, "EXPENSE", "출장비",
                List.of(10L, 20L, 30L));

        assertThat(d.getStatus()).isEqualTo(ApprovalDocument.Status.IN_PROGRESS);
        assertThat(d.getCurrentStep()).isZero();
    }

    @Test
    void draft_시_ApprovalRequestedEvent_가_등록됨() {
        ApprovalDocument d = ApprovalDocument.draft(1L, "EXPENSE", "출장비",
                List.of(10L, 20L));

        assertThat(d.events()).hasAtLeastOneElementOfType(ApprovalRequestedEvent.class);
    }

    @Test
    void 순차_결재_전원_승인시_APPROVED_와_완료_이벤트() {
        ApprovalDocument d = ApprovalDocument.draft(1L, "EXPENSE", "출장비",
                List.of(10L, 20L, 30L));
        d.assignId(100L);
        d.pullEvents(); // draft 이벤트 제거

        d.approve(10L);
        d.approve(20L);
        d.approve(30L);

        assertThat(d.getStatus()).isEqualTo(ApprovalDocument.Status.APPROVED);
        assertThat(d.getCurrentStep()).isEqualTo(3);
        assertThat(d.events())
                .filteredOn(e -> e instanceof ApprovalCompletedEvent)
                .extracting(e -> ((ApprovalCompletedEvent) e).approved())
                .containsExactly(true);
    }

    @Test
    void 중간_단계_승인_후에는_IN_PROGRESS_유지_완료_이벤트_없음() {
        ApprovalDocument d = ApprovalDocument.draft(1L, "EXPENSE", "-",
                List.of(10L, 20L, 30L));
        d.pullEvents();

        d.approve(10L);

        assertThat(d.getStatus()).isEqualTo(ApprovalDocument.Status.IN_PROGRESS);
        assertThat(d.getCurrentStep()).isOne();
        assertThat(d.events()).doesNotHaveAnyElementsOfTypes(ApprovalCompletedEvent.class);
    }

    @Test
    void 잘못된_순서로_결재_시도시_예외() {
        ApprovalDocument d = ApprovalDocument.draft(1L, "EXPENSE", "-",
                List.of(10L, 20L));

        // 현재 결재자는 10L 인데 20L 이 승인 시도
        assertThatThrownBy(() -> d.approve(20L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("현재 결재자 아님");
    }

    @Test
    void 현재_결재자가_아닌_사람의_반려도_예외() {
        ApprovalDocument d = ApprovalDocument.draft(1L, "EXPENSE", "-",
                List.of(10L, 20L));

        assertThatThrownBy(() -> d.reject(20L, "사유"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void reject_시_즉시_REJECTED_와_완료_이벤트_approved_false() {
        ApprovalDocument d = ApprovalDocument.draft(1L, "EXPENSE", "-",
                List.of(10L, 20L, 30L));
        d.assignId(100L);
        d.pullEvents();

        d.reject(10L, "근거 부족");

        assertThat(d.getStatus()).isEqualTo(ApprovalDocument.Status.REJECTED);
        assertThat(d.events())
                .filteredOn(e -> e instanceof ApprovalCompletedEvent)
                .extracting(e -> ((ApprovalCompletedEvent) e).approved())
                .containsExactly(false);
    }

    @Test
    void 마지막_단계에서_반려도_REJECTED() {
        ApprovalDocument d = ApprovalDocument.draft(1L, "EXPENSE", "-",
                List.of(10L, 20L));
        d.approve(10L);

        d.reject(20L, "최종 단계에서 반려");

        assertThat(d.getStatus()).isEqualTo(ApprovalDocument.Status.REJECTED);
    }

    @Test
    void 결재자_1명_단일_단계_정책_승인시_즉시_APPROVED() {
        ApprovalDocument d = ApprovalDocument.draft(1L, "LEAVE", "연차",
                List.of(10L));

        d.approve(10L);

        assertThat(d.getStatus()).isEqualTo(ApprovalDocument.Status.APPROVED);
        assertThat(d.getCurrentStep()).isOne();
    }

    @Test
    void 결재자_3명_중간_단계_승인자_순서_고정() {
        // 단계별 승인 정책: steps[0]=10, steps[1]=20, steps[2]=30
        ApprovalDocument d = ApprovalDocument.draft(1L, "EXPENSE", "-",
                List.of(10L, 20L, 30L));

        d.approve(10L);
        // 현재 단계는 20L. 30L 이 먼저 승인하려 하면 예외
        assertThatThrownBy(() -> d.approve(30L))
                .isInstanceOf(IllegalStateException.class);

        d.approve(20L);
        assertThatThrownBy(() -> d.approve(10L))  // 이미 처리된 결재자 재호출
                .isInstanceOf(IllegalStateException.class);
    }
}