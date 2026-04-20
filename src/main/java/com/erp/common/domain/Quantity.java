package com.erp.common.domain;

public final class Quantity {

    public static final Quantity ZERO = new Quantity(0);

    private final int value;

    public Quantity(int value) {
        if (value < 0) throw new IllegalArgumentException("quantity must be >= 0, got " + value);
        this.value = value;
    }

    public static Quantity of(int v) { return new Quantity(v); }
    public int value() { return value; }

    public Quantity add(Quantity other) { return new Quantity(this.value + other.value); }

    public Quantity subtract(Quantity other) {
        if (this.value < other.value) {
            throw new IllegalArgumentException("cannot subtract larger quantity");
        }
        return new Quantity(this.value - other.value);
    }

    public boolean isZero() { return value == 0; }
    public boolean isGreaterThanOrEqual(Quantity other) { return this.value >= other.value; }

    @Override public boolean equals(Object o) {
        return o instanceof Quantity q && q.value == this.value;
    }
    @Override public int hashCode() { return Integer.hashCode(value); }
    @Override public String toString() { return String.valueOf(value); }
}