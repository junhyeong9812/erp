package com.erp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

@SpringBootTest
class ErpApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void verifyModules() {
        ApplicationModules.of(ErpApplication.class).verify();
    }

    @Test
    void writeDocumentation() {
        ApplicationModules modules = ApplicationModules.of(ErpApplication.class);
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }
}