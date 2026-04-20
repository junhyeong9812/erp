package com.erp.common.domain;

import java.time.LocalDate;
import java.util.Objects;

public final class Period {

    private final LocalDate start;
    private final LocalDate end;

    public Period(LocalDate start, LocalDate end) {
        Objects.requireNonNull(start, "start");
        Objects.requireNonNull(end, "end");
        if (end.isBefore(start)) throw new IllegalArgumentException("end before start");
        this.start = start;
        this.end = end;
    }

    public LocalDate start() { return start; }
    public LocalDate end()   { return end; }

    public boolean contains(LocalDate date) {
        return !date.isBefore(start) && !date.isAfter(end);
    }
}