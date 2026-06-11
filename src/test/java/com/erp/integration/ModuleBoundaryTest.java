package com.erp.integration;

import com.erp.ErpApplication;
import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModuleBoundaryTest {

    @Test
    void 모듈_경계_위반_없음() {
        ApplicationModules.of(ErpApplication.class).verify();
    }

    @Test
    void 모듈_다이어그램_생성() {
        ApplicationModules modules = ApplicationModules.of(ErpApplication.class);
        new Documenter(modules)
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
        // 결과: target/spring-modulith-docs/*.puml
    }

    @Test
    void 모듈_목록_출력() {
        ApplicationModules.of(ErpApplication.class)
                .forEach(m -> System.out.println(m.getName() + " — " + m.getDisplayName()));
    }
}