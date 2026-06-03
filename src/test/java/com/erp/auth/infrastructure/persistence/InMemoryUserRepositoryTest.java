package com.erp.auth.infrastructure.persistence;

import com.erp.auth.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryUserRepositoryTest {

    private InMemoryUserRepository repo;

    @BeforeEach
    void setUp() { repo = new InMemoryUserRepository(); }

    @Test
    void save_시_id_가_null_이면_IdGenerator_로_자동_부여() {
        User u = User.register("alice", "HASH:x", 1L);
        assertThat(u.getId()).isNull();

        repo.save(u);

        assertThat(u.getId()).isNotNull();
        assertThat(repo.findById(u.getId())).isPresent();
    }

    @Test
    void findByUsername_으로_조회() {
        User u = User.register("alice", "HASH:x", 1L);
        repo.save(u);

        assertThat(repo.findByUsername("alice")).isPresent();
        assertThat(repo.findByUsername("nobody")).isEmpty();
    }

    @Test
    void 같은_id_로_save_두_번이면_덮어쓰기() {
        User u = User.register("alice", "HASH:x", 1L);
        repo.save(u);
        Long id = u.getId();

        User updated = User.register("alice2", "HASH:y", 1L);
        updated.assignId(id);
        repo.save(updated);

        assertThat(repo.findById(id).get().getUsername()).isEqualTo("alice2");
    }
}