package com.erp.common.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public final class Money {

    public static final Money ZERO = new Money(BigDecimal.ZERO);

    private final BigDecimal amount;

    public Money(BigDecimal amount) {
        Objects.requireNonNull(amount, "amount must not be null");
        this.amount = amount.setScale(0, RoundingMode.HALF_UP);
    }

    public static Money of(long value) {
        return new Money(BigDecimal.valueOf(value));
    }

    public BigDecimal amount() { return amount; }

    public Money add(Money other)      { return new Money(this.amount.add(other.amount)); }
    public Money subtract(Money other) { return new Money(this.amount.subtract(other.amount)); }
    public Money multiply(int times)   { return new Money(this.amount.multiply(BigDecimal.valueOf(times))); }

    public boolean isNegative()            { return amount.signum() < 0; }
    public boolean isGreaterThan(Money o)  { return amount.compareTo(o.amount) > 0; }

    @Override public boolean equals(Object o) {
        if (!(o instanceof Money m)) return false;
        return amount.compareTo(m.amount) == 0;
    }
    @Override public int hashCode() { return amount.hashCode(); }
    @Override public String toString() { return amount.toPlainString() + " KRW"; }
}