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

    public static SalesOrder place(Long customerId, Long quoteId, List<SalesOrderItem> items) {
        SalesOrder o = new SalesOrder();
        o.customerId = customerId;
        o.quoteId = quoteId;
        o.items = new ArrayList<>(items);
        o.totalAmount = items.stream()
                .mapToLong(i -> i.getSubtotal().amount().longValueExact())
                .sum();
        o.status = Status.PLACED;
        o.register(new SalesOrderPlacedEvent(
                null, customerId,
                items.stream()
                        .map(i -> new SalesOrderPlacedEvent.Line(i.getProductId(), i.getQuantity()))
                        .toList(),
                Instant.now()));
        return o;
    }

    public void assignId(Long id) {
        this.id = id;
        // 이벤트의 orderId 를 뒤늦게 채우려면 발행 시 다시 만들어라.
    }

    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public Money getTotalAmount() { return Money.of(totalAmount); }
    public Status getStatus() { return status; }
    public List<SalesOrderItem> getItems() { return Collections.unmodifiableList(items); }

    public enum Status { PLACED, CONFIRMED, SHIPPED, COMPLETED, CANCELLED }
}