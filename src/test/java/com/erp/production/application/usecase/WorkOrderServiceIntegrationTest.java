package com.erp.production.application.usecase;

import com.erp.production.application.dto.command.IssueWorkOrderCommand;
import com.erp.production.application.port.inbound.WorkOrderUseCase;
import com.erp.production.domain.event.ProductionCompletedEvent;
import com.erp.production.domain.event.WorkOrderIssuedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.event.EventListener;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ApplicationModuleTest
class WorkOrderServiceIntegrationTest {

    @Autowired WorkOrderUseCase useCase;
    @Autowired EventCollector collector;

    @Component
    static class EventCollector {
        final List<Object> events = new ArrayList<>();
        @EventListener
        public void onIssued(WorkOrderIssuedEvent e) { events.add(e); }
        @EventListener
        public void onCompleted(ProductionCompletedEvent e) { events.add(e); }
    }

    @Test
    void issueWorkOrder_후_recordProduction_계획량_도달_시_두_이벤트_순서대로_발행() {
        Long id = useCase.issueWorkOrder(new IssueWorkOrderCommand(100L, 30));
        useCase.recordProduction(id, 30, 0);

        assertThat(collector.events)
                .hasSize(2)
                .extracting(Object::getClass)
                .containsExactly(WorkOrderIssuedEvent.class, ProductionCompletedEvent.class);
    }
}