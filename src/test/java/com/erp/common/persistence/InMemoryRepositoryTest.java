package com.erp.common.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InMemoryRepositoryTest {

    static class User {
        Long id; String name;
        User(Long id, String name) { this.id = id; this.name = name; }
    }

    static class UserRepo extends InMemoryRepository<User, Long> {
        @Override protected Long extractId(User entity) { return entity.id; }
    }

    private UserRepo repo;

    @BeforeEach
    void setUp() { repo = new UserRepo(); }

    @Test
    void save_후_findById_로_조회() {
        repo.save(new User(1L, "alice"));
        assertThat(repo.findById(1L)).isPresent().get().extracting(u -> u.name).isEqualTo("alice");
    }

    @Test
    void id_가_null_이면_save_시_예외() {
        assertThatThrownBy(() -> repo.save(new User(null, "noid")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("entity id must be assigned");
    }

    @Test
    void findById_없는_id_는_Optional_empty() {
        assertThat(repo.findById(999L)).isEmpty();
    }

    @Test
    void findAllBy_는_Predicate_필터링() {
        repo.save(new User(1L, "alice"));
        repo.save(new User(2L, "bob"));
        repo.save(new User(3L, "alice"));

        var alices = repo.findAllBy(u -> u.name.equals("alice"));
        assertThat(alices).hasSize(2);
    }

    @Test
    void deleteById_후_existsById_거짓() {
        repo.save(new User(1L, "x"));
        repo.deleteById(1L);
        assertThat(repo.existsById(1L)).isFalse();
        assertThat(repo.count()).isZero();
    }

    @Test
    void clear_는_전체_삭제() {
        repo.save(new User(1L, "a"));
        repo.save(new User(2L, "b"));
        repo.clear();
        assertThat(repo.count()).isZero();
    }

    @Test
    void save_같은_id_두번이면_덮어쓰기() {
        repo.save(new User(1L, "old"));
        repo.save(new User(1L, "new"));
        assertThat(repo.findById(1L).get().name).isEqualTo("new");
        assertThat(repo.count()).isOne();
    }

    @Test
    void 동시_save_1000건_손실없이_저장() throws InterruptedException {
        ExecutorService pool = Executors.newFixedThreadPool(16);
        CountDownLatch latch = new CountDownLatch(1000);
        IntStream.range(0, 1000).forEach(i -> pool.submit(() -> {
            try { repo.save(new User((long) i, "u" + i)); }
            finally { latch.countDown(); }
        }));
        boolean result = latch.await(5, TimeUnit.SECONDS);
        pool.shutdown();
        assertThat(result).isTrue();
        assertThat(repo.count()).isEqualTo(1000);
    }

    @Test
    void findAll_은_내부_Map_의_스냅샷() {
        repo.save(new User(1L, "a"));
        var all = repo.findAll();
        repo.save(new User(2L, "b"));

        // findAll() 이 ArrayList 로 복사했으므로 이후 save 는 반영 안 됨
        assertThat(all).hasSize(1);
    }
}