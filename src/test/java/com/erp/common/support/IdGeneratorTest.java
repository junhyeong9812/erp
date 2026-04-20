package com.erp.common.support;

import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

    @Test
    void 연속호출시_단조증가() {
        long a = IdGenerator.next();
        long b = IdGenerator.next();
        long c = IdGenerator.next();
        assertThat(b).isGreaterThan(a);
        assertThat(c).isGreaterThan(b);
    }

    @Test
    void 동시_호출_1만건_모두_unique() throws InterruptedException {
        int N = 10_000;
        ConcurrentHashMap<Long, Boolean> seen = new ConcurrentHashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(N);
        AtomicLong collisions = new AtomicLong();

        IntStream.range(0, N).forEach(i -> pool.submit(() -> {
            try {
                long id = IdGenerator.next();
                if (seen.putIfAbsent(id, true) != null) collisions.incrementAndGet();
            } finally { latch.countDown(); }
        }));
        boolean finished = latch.await(10, TimeUnit.SECONDS);
        pool.shutdown();

        assertThat(finished).isTrue();
        assertThat(collisions.get()).isZero();
    }
}