package com.erp.settlement.domain.entity;

import com.erp.common.domain.BaseEntity;
import com.erp.common.domain.Money;
import com.erp.common.exception.ConflictException;
import com.erp.settlement.domain.exception.SettlementErrorCode;
import jakarta.persistence.*;

@Entity
@Table(name = "seller_settlement")
public class SellerSettlement extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;
    private Long periodId;

    private long grossSales;     // 기간 매출 합계
    private long refundAmount;   // 기간 환불 합계
    private long feeAmount;      // 플랫폼 수수료
    private long netPayout;      // = gross - refund - fee

    @Enumerated(EnumType.STRING)
    private Status status;

    protected SellerSettlement() {}

    public static SellerSettlement calculate(Long sellerId, Long periodId,
                                             Money gross, Money refund, Money fee) {
        Money net = gross.subtract(refund).subtract(fee);
        if (net.isNegative()) {
            throw new ConflictException(SettlementErrorCode.SELLER_NET_NEGATIVE,
                    "seller=" + sellerId + " net=" + net);
        }
        SellerSettlement s = new SellerSettlement();
        s.sellerId = sellerId;
        s.periodId = periodId;
        s.grossSales = gross.amount().longValueExact();
        s.refundAmount = refund.amount().longValueExact();
        s.feeAmount = fee.amount().longValueExact();
        s.netPayout = net.amount().longValueExact();
        s.status = Status.CALCULATED;
        return s;
    }

    public void markPaid() {
        if (status != Status.CALCULATED) {
            throw new ConflictException(SettlementErrorCode.SELLER_SETTLEMENT_ALREADY_PAID);
        }
        this.status = Status.PAID;
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public Long getSellerId() { return sellerId; }
    public Long getPeriodId() { return periodId; }
    public Money getGrossSales() { return Money.of(grossSales); }
    public Money getRefundAmount() { return Money.of(refundAmount); }
    public Money getFeeAmount() { return Money.of(feeAmount); }
    public Money getNetPayout() { return Money.of(netPayout); }
    public Status getStatus() { return status; }

    public enum Status { CALCULATED, PAID }
}