package com.erp.promotion.presentation.api;

import com.erp.promotion.application.dto.command.EarnPointCommand;
import com.erp.promotion.application.port.inbound.PointUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean
    PointUseCase useCase;

    @Test
    void POST_earn_요청시_UseCase_호출하고_id_반환() throws Exception {
        given(useCase.earn(any(EarnPointCommand.class))).willReturn(10L);

        String body = """
            {"customerId":1,"amount":500,"expireOn":"2030-01-01"}
            """;

        mvc.perform(post("/api/promotion/points/earn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }
}