package com.erp.sales.application.usecase;

import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.common.domain.Money;
import com.erp.sales.application.dto.command.PlaceOrderCommand;
import com.erp.sales.application.port.inbound.SalesOrderUseCase;
import com.erp.sales.application.port.outbound.SalesOrderRepository;
import com.erp.sales.domain.entity.SalesOrder;
import com.erp.sales.domain.entity.SalesOrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SalesOrderService implements SalesOrderUseCase {

    private final SalesOrderRepository orderRepository;
    private final EventBus eventBus;

    public SalesOrderService(SalesOrderRepository orderRepository, EventBus eventBus) {
        this.orderRepository = orderRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Long placeOrder(PlaceOrderCommand cmd) {
        var items = cmd.lines().stream()
                .map(l -> SalesOrderItem.of(l.productId(), l.quantity(), Money.of(l.unitPrice())))
                .toList();
        SalesOrder order = SalesOrder.place(cmd.customerId(), cmd.quoteId(), items);
        order.assignId(IdGenerator.next());
        orderRepository.save(order);
        eventBus.publishAll(order.pullEvents());
        return order.getId();
    }
}