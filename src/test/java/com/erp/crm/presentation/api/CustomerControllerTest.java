package com.erp.crm.presentation.api;

import com.erp.crm.application.dto.command.RegisterCustomerCommand;
import com.erp.crm.application.port.inbound.CustomerUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;
    @MockitoBean
    CustomerUseCase customerUseCase;

    @Test
    void POST_api_crm_customers_는_Command_를_UseCase_에_전달하고_id_반환() throws Exception {
        when(customerUseCase.register(any(RegisterCustomerCommand.class))).thenReturn(42L);

        String body = """
            {
              "customerCode": "C001",
              "name": "ACME",
              "contact": "01012345678",
              "assignedSalesEmployeeId": 1,
              "creditLimit": 1000000
            }
            """;

        mvc.perform(post("/api/crm/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(content().string("42"));

        verify(customerUseCase).register(argThat((RegisterCustomerCommand c) ->
                c.customerCode().equals("C001")
                        && c.name().equals("ACME")
                        && c.contact().equals("01012345678")
                        && c.assignedSalesEmployeeId() == 1L
                        && c.creditLimit() == 1_000_000L));
    }
}