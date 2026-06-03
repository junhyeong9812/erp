package com.erp.auth.infrastructure.persistence;

import com.erp.auth.domain.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryRoleRepositoryTest {

    private InMemoryRoleRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryRoleRepository(); }

    @Test
    void save_시_id_자동_부여_후_findByCode_로_조회() {
        Role r = Role.of("ROLE_SALES", "영업");
        repo.save(r);

        assertThat(r.getId()).isNotNull();
        assertThat(repo.findByCode("ROLE_SALES")).isPresent();
    }

    @Test
    void findByCode_없으면_Optional_empty() {
        assertThat(repo.findByCode("ROLE_NONE")).isEmpty();
    }
}