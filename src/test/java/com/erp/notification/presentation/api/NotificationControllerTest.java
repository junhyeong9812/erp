package com.erp.notification.presentation.api;

import com.erp.notification.application.dto.command.SendNotificationCommand;
import com.erp.notification.application.port.inbound.NotificationUseCase;
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

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired MockMvc mvc;
    @MockitoBean
    NotificationUseCase useCase;

    @Test
    void POST_알림_요청시_UseCase_호출하고_id_반환() throws Exception {
        given(useCase.send(any(SendNotificationCommand.class))).willReturn(42L);

        String body = """
            {"recipientId":1,"title":"hi","body":"hello","channel":"EMAIL"}
            """;

        mvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));
    }
}