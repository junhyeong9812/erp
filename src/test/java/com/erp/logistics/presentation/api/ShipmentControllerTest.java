package com.erp.logistics.presentation.api;

import com.erp.logistics.application.dto.command.DispatchShipmentCommand;
import com.erp.logistics.application.port.inbound.ShipmentUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShipmentController.class)
class ShipmentControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean
    ShipmentUseCase useCase;

    @Test
    void POST_dispatch_는_UseCase_에_DispatchShipmentCommand_전달() throws Exception {
        String body = om.writeValueAsString(new java.util.HashMap<String, Object>() {{
            put("driverId", "driver-1");
            put("trackingNumber", "TRK-001");
        }});

        mvc.perform(post("/api/logistics/shipments/100/dispatch")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        verify(useCase).dispatch(new DispatchShipmentCommand(100L, "driver-1", "TRK-001"));
    }
}