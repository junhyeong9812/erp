package com.erp.production.presentation.api;

import com.erp.production.application.dto.command.IssueWorkOrderCommand;
import com.erp.production.application.port.inbound.WorkOrderUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WorkOrderController.class)
class WorkOrderControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean
    WorkOrderUseCase useCase;

    @Test
    void POST_work_orders_는_UseCase_에_IssueWorkOrderCommand_전달() throws Exception {
        when(useCase.issueWorkOrder(new IssueWorkOrderCommand(100L, 50))).thenReturn(1L);

        String body = om.writeValueAsString(new java.util.HashMap<String, Object>() {{
            put("productId", 100);
            put("plannedQuantity", 50);
        }});

        mvc.perform(post("/api/production/work-orders")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        verify(useCase).issueWorkOrder(new IssueWorkOrderCommand(100L, 50));
    }
}