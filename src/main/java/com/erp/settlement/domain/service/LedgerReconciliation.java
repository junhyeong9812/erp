package com.erp.settlement.domain.service;

import com.erp.common.domain.Money;
import com.erp.settlement.domain.entity.Ledger;

import java.util.*;
import java.util.stream.Collectors;

public final class LedgerReconciliation {

    public record Result(Money totalDebit, Money totalCredit,
                         boolean balanced, List<Ledger> unbalancedRefs) {}

    private LedgerReconciliation() {}

    public static Result verify(List<Ledger> ledgers) {
        // reference 단위 그루핑 — 동일 거래에서 debit/credit 이 모두 발생한 경우만 정합성 비교
        Map<String, List<Ledger>> byRef = ledgers.stream()
                .filter(l -> l.getType() == Ledger.Type.ADJUSTMENT
                        || l.getType() == Ledger.Type.REVERSAL)
                .collect(Collectors.groupingBy(l -> l.getReferenceType() + ":" + l.getReferenceId()));

        List<Ledger> unbalanced = byRef.values().stream()
                .filter(group -> {
                    long d = group.stream().mapToLong(l -> l.getDebit().amount().longValueExact()).sum();
                    long c = group.stream().mapToLong(l -> l.getCredit().amount().longValueExact()).sum();
                    return d != c;
                })
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Money totalDebit = ledgers.stream().map(Ledger::getDebit).reduce(Money.ZERO, Money::add);
        Money totalCredit = ledgers.stream().map(Ledger::getCredit).reduce(Money.ZERO, Money::add);

        return new Result(totalDebit, totalCredit, unbalanced.isEmpty(), unbalanced);
    }
}