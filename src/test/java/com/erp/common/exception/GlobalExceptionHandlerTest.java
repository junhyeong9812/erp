package com.erp.common.exception;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.Dummy.class)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

    @RestController
    static class Dummy {
        @GetMapping("/t/business")   void biz()     { throw new BusinessException(CommonErrorCode.NOT_FOUND); }
        @GetMapping("/t/illegal")    void illegal() { throw new IllegalArgumentException("bad input"); }
        @GetMapping("/t/unexpected") void unexp()   { throw new RuntimeException("boom"); }
    }

    @Autowired MockMvc mvc;

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