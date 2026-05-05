package com.erp.common.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    @RestController
    static class Dummy {
        @GetMapping("/t/business")   void biz()     { throw new BusinessException(CommonErrorCode.NOT_FOUND); }
        @GetMapping("/t/illegal")    void illegal() { throw new IllegalArgumentException("bad input"); }
        @GetMapping("/t/unexpected") void unexp()   { throw new RuntimeException("boom"); }
    }

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        // standaloneSetup 으로 advice 를 명시적으로 등록 — @WebMvcTest + @Import 가 advice
        // 를 인식하지 못하는 Spring Boot 3.5 환경에서도 안정적으로 동작.
        mvc = MockMvcBuilders.standaloneSetup(new Dummy())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void BusinessException_은_해당_ErrorCode_상태코드로_응답() throws Exception {
        mvc.perform(get("/t/business"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("COMMON-002"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void IllegalArgumentException_은_400() throws Exception {
        mvc.perform(get("/t/illegal"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("COMMON-001"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("bad input")));
    }

    @Test
    void 알수없는_예외는_500() throws Exception {
        mvc.perform(get("/t/unexpected"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("COMMON-999"));
    }
}
