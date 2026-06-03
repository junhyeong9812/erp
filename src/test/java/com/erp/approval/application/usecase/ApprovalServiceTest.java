package com.erp.approval.application.usecase;

import com.erp.approval.application.dto.command.ApproveCommand;
import com.erp.approval.application.dto.command.DraftApprovalCommand;
import com.erp.approval.application.port.outbound.ApprovalDocumentRepository;
import com.erp.approval.domain.entity.ApprovalDocument;
import com.erp.approval.domain.event.ApprovalCompletedEvent;
import com.erp.approval.domain.event.ApprovalRequestedEvent;
import com.erp.approval.infrastructure.persistence.InMemoryApprovalDocumentRepository;
import com.erp.common.domain.DomainEvent;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ApprovalServiceTest {

    static class RecordingEventBus implements EventBus {
        final List<DomainEvent> published = new ArrayList<>();
        @Override public void publish(DomainEvent event) { published.add(event); }
        @Override
        public void publishAll(Iterable<? extends DomainEvent> events) {
            events.forEach(published::add);
        }
    }

    private ApprovalDocumentRepository repo;
    private RecordingEventBus bus;
    private ApprovalService service;

    @BeforeEach
    void setUp() {
        repo = new InMemoryApprovalDocumentRepository();
        bus = new RecordingEventBus();
        service = new ApprovalService(repo, bus);
    }

    @Test
    void draft_는_저장하고_ApprovalRequestedEvent_발행() {
        Long id = service.draft(new DraftApprovalCommand(
                1L, "EXPENSE", "출장비", List.of(10L, 20L)));

        assertThat(id).isNotNull();
        assertThat(repo.findById(id)).isPresent();
        assertThat(bus.published).hasAtLeastOneElementOfType(ApprovalRequestedEvent.class);
    }

    @Test
    void draft_후_aggregate_의_이벤트는_비워져_있음() {
        Long id = service.draft(new DraftApprovalCommand(
                1L, "EXPENSE", "-", List.of(10L)));

        ApprovalDocument d = repo.findById(id).orElseThrow();
        assertThat(d.events()).isEmpty();
    }

    @Test
    void approve_순차_전원_승인시_APPROVED_와_완료_이벤트() {
        Long id = service.draft(new DraftApprovalCommand(
                1L, "EXPENSE", "-", List.of(10L, 20L)));

        service.approve(new ApproveCommand(id, 10L));
        service.approve(new ApproveCommand(id, 20L));

        ApprovalDocument d = repo.findById(id).orElseThrow();
        assertThat(d.getStatus()).isEqualTo(ApprovalDocument.Status.APPROVED);
        assertThat(bus.published)
                .filteredOn(e -> e instanceof ApprovalCompletedEvent)
                .extracting(e -> ((ApprovalCompletedEvent) e).approved())
                .containsExactly(true);
    }

    @Test
    void 중간_단계_승인시_IN_PROGRESS_유지_완료_이벤트_없음() {
        Long id = service.draft(new DraftApprovalCommand(
                1L, "EXPENSE", "-", List.of(10L, 20L, 30L)));
        bus.published.clear(); // draft 이벤트 제거

        service.approve(new ApproveCommand(id, 10L));

        ApprovalDocument d = repo.findById(id).orElseThrow();
        assertThat(d.getStatus()).isEqualTo(ApprovalDocument.Status.IN_PROGRESS);
        assertThat(bus.published).doesNotHaveAnyElementsOfTypes(ApprovalCompletedEvent.class);
    }

    @Test
    void reject_는_REJECTED_와_완료_이벤트_approved_false() {
        Long id = service.draft(new DraftApprovalCommand(
                1L, "EXPENSE", "-", List.of(10L, 20L)));

        service.reject(id, 10L, "증빙 부족");

        ApprovalDocument d = repo.findById(id).orElseThrow();
        assertThat(d.getStatus()).isEqualTo(ApprovalDocument.Status.REJECTED);
        assertThat(bus.published)
                .filteredOn(e -> e instanceof ApprovalCompletedEvent)
                .extracting(e -> ((ApprovalCompletedEvent) e).approved())
                .containsExactly(false);
    }

    @Test
    void 잘못된_순서_approve_는_IllegalStateException() {
        Long id = service.draft(new DraftApprovalCommand(
                1L, "EXPENSE", "-", List.of(10L, 20L)));

        assertThatThrownBy(() -> service.approve(new ApproveCommand(id, 20L)))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void 존재하지_않는_문서_approve_는_NotFoundException() {
        assertThatThrownBy(() -> service.approve(new ApproveCommand(9999L, 10L)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void 존재하지_않는_문서_reject_는_NotFoundException() {
        assertThatThrownBy(() -> service.reject(9999L, 10L, "x"))
                .isInstanceOf(NotFoundException.class);
    }
}