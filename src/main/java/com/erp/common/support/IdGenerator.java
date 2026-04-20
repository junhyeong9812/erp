package com.erp.common.support;

import java.util.concurrent.atomic.AtomicLong;

public final class IdGenerator {

    private static final AtomicLong GLOBAL = new AtomicLong(0);

    private IdGenerator() {}

    public static long next() {
        return GLOBAL.incrementAndGet();
    }
}