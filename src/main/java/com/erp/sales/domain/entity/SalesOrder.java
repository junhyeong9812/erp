package com.erp.sales.domain.entity;

import com.erp.common.domain.AggregateRoot;
import com.erp.common.domain.Money;
import com.erp.sales.domain.event.SalesOrderPlacedEvent;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "sales_order")
public class SalesOrder extends AggregateRoot {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long quoteId;
    private long totalAmount;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<SalesOrderItem> items = new ArrayList<>();

    protected SalesOrder() {}

    /**
     * Aggregate 생성. 이벤트는 등록하지 않는다 — 이 시점에는 id 가 없으므로
     * orderId 가 null 인 이벤트가 발행되는 문제가 있어, 호출자가 {@link #assignId(Long)}
     * 후에 {@link #registerPlacedEvent()} 를 명시적으로 호출하도록 분리.
     */
    public static SalesOrder place(Long customerId, Long quoteId, List<SalesOrderItem> items) {
        SalesOrder o = new SalesOrder();
        o.customerId = customerId;
        o.quoteId = quoteId;
        o.items = new ArrayList<>(items);
        o.totalAmount = items.stream()
                .mapToLong(i -> i.getSubtotal().amount().longValueExact())
                .sum();
        o.status = Status.PLACED;
        return o;
    }

    /** {@link #assignId(Long)} 후 호출. orderId 가 채워진 PlacedEvent 를 등록. */
    public void registerPlacedEvent() {
        if (id == null) throw new IllegalStateException("registerPlacedEvent는 assignId 이후에 호출되어야 한다");
        register(new SalesOrderPlacedEvent(
                this.id, this.customerId,
                this.items.stream()
                        .map(i -> new SalesOrderPlacedEvent.Line(i.getProductId(), i.getQuantity()))
                        .toList(),
                Instant.now()));
    }

    public void confirm() {
        if (status != Status.PLACED) throw new IllegalStateException("PLACED 상태만 확정 가능: " + status);
        this.status = Status.CONFIRMED;
    }

    public void ship() {
        if (status != Status.CONFIRMED) throw new IllegalStateException("CONFIRMED 상태만 배송 시작 가능: " + status);
        this.status = Status.SHIPPED;
    }

    public void complete() {
        if (status != Status.SHIPPED) throw new IllegalStateException("SHIPPED 상태만 완료 가능: " + status);
        this.status = Status.COMPLETED;
    }

    public void cancel() {
        if (status == Status.COMPLETED || status == Status.CANCELLED)
            throw new IllegalStateException("이미 종결된 주문은 취소 불가: " + status);
        this.status = Status.CANCELLED;
    }

    public void assignId(Long id) { this.id = id; }

    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public Long getQuoteId() { return quoteId; }
    public Money getTotalAmount() { return Money.of(totalAmount); }
    public Status getStatus() { return status; }
    public List<SalesOrderItem> getItems() { return Collections.unmodifiableList(items); }

    public enum Status { PLACED, CONFIRMED, SHIPPED, COMPLETED, CANCELLED }
}