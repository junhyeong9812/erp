package com.erp.settlement.domain.entity;

import com.erp.common.domain.BaseEntity;
import com.erp.common.domain.Money;
import jakarta.persistence.*;

@Entity
@Table(name = "settlement_ledger")
public class Ledger extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private Type type;

    private String referenceType;
    private Long referenceId;

    private long debit;
    private long credit;

    @Column(length = 500)
    private String description;

    private Long periodId;

    protected Ledger() {}

    public static Ledger sales(Long refId, Money amount, String desc, Long periodId) {
        Ledger l = new Ledger();
        l.type = Type.SALES;
        l.referenceType = "PAYMENT";
        l.referenceId = refId;
        l.credit = amount.amount().longValueExact();
        l.description = desc;
        l.periodId = periodId;
        return l;
    }

    public static Ledger refund(Long refId, Money amount, String desc, Long periodId) {
        Ledger l = new Ledger();
        l.type = Type.REFUND;
        l.referenceType = "REFUND";
        l.referenceId = refId;
        l.debit = amount.amount().longValueExact();
        l.description = desc;
        l.periodId = periodId;
        return l;
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public Type getType() { return type; }
    public Money getDebit() { return Money.of(debit); }
    public Money getCredit() { return Money.of(credit); }
    public Long getPeriodId() { return periodId; }

    public enum Type { SALES, PURCHASE, REFUND, FEE, ADJUSTMENT, REVERSAL }

    // --- 결산 심화 추가 팩토리 ---

    /** 연말/분기 조정분개 (감가상각, 재평가, 대손 등). 차변/대변을 명시적으로 지정. */
    public static Ledger adjustment(Long refId, String refType,
                                    Money debit, Money credit,
                                    String desc, Long periodId) {
        Ledger l = new Ledger();
        l.type = Type.ADJUSTMENT;
        l.referenceType = refType;
        l.referenceId = refId;
        l.debit = debit.amount().longValueExact();
        l.credit = credit.amount().longValueExact();
        l.description = desc;
        l.periodId = periodId;
        return l;
    }

    /** 반대전표(취소) — 원본 Ledger 의 debit/credit 을 뒤집어 기록. */
    public static Ledger reverseOf(Ledger original, String reason, Long periodId) {
        Ledger l = new Ledger();
        l.type = Type.REVERSAL;
        l.referenceType = "LEDGER";
        l.referenceId = original.getId();
        l.debit = original.credit;   // 뒤집기
        l.credit = original.debit;
        l.description = "REVERSAL: " + reason + " (of #" + original.getId() + ")";
        l.periodId = periodId;
        return l;
    }

    public String getReferenceType() { return referenceType; }
    public Long getReferenceId() { return referenceId; }
}