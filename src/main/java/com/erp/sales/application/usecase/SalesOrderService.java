package com.erp.sales.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.ConflictException;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.sales.application.dto.command.PlaceOrderCommand;
import com.erp.sales.application.dto.query.SalesOrderQuery;
import com.erp.sales.application.port.inbound.SalesOrderUseCase;
import com.erp.sales.application.port.outbound.QuoteRepository;
import com.erp.sales.application.port.outbound.SalesOrderRepository;
import com.erp.sales.domain.entity.Quote;
import com.erp.sales.domain.entity.SalesOrder;
import com.erp.sales.domain.entity.SalesOrderItem;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class SalesOrderService implements SalesOrderUseCase {

    private final SalesOrderRepository orderRepository;
    private final QuoteRepository quoteRepository;
    private final EventBus eventBus;

    public SalesOrderService(SalesOrderRepository orderRepository,
                             QuoteRepository quoteRepository,
                             EventBus eventBus) {
        this.orderRepository = orderRepository;
        this.quoteRepository = quoteRepository;
        this.eventBus = eventBus;
    }

    @Override
    public Long placeOrder(PlaceOrderCommand cmd) {
        // quoteId 가 주어진 경우에만 견적 검증 + 수락. null 이면 견적 없는 직접 주문.
        if (cmd.quoteId() != null) {
            Quote quote = quoteRepository.findById(cmd.quoteId())
                    .orElseThrow(NotFoundException::new);
            // ACTIVE 면 자동 수락, ACCEPTED 면 통과, 그 외(EXPIRED/REJECTED)는 거부
            if (quote.getStatus() == Quote.Status.ACTIVE) {
                quote.accept();
                quoteRepository.save(quote);
            } else if (quote.getStatus() != Quote.Status.ACCEPTED) {
                throw new ConflictException();
            }
        }

        var items = cmd.lines().stream()
                .map(l -> SalesOrderItem.of(l.productId(), l.quantity(), Money.of(l.unitPrice())))
                .toList();
        SalesOrder order = SalesOrder.place(cmd.customerId(), cmd.quoteId(), items);
        order.assignId(IdGenerator.next());
        order.registerPlacedEvent();
        orderRepository.save(order);
        eventBus.publishAll(order.pullEvents());
        return order.getId();
    }

    @Override
    public SalesOrderQuery findById(Long orderId) {
        SalesOrder order = orderRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        return new SalesOrderQuery(
                order.getId(),
                order.getCustomerId(),
                order.getQuoteId(),
                order.getTotalAmount().amount().longValueExact(),
                order.getStatus().name());
    }

    @Override
    public void confirm(Long orderId) {
        SalesOrder order = orderRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        order.confirm();
        orderRepository.save(order);
    }

    @Override
    public void cancel(Long orderId) {
        SalesOrder order = orderRepository.findById(orderId)
                .orElseThrow(NotFoundException::new);
        order.cancel();
        orderRepository.save(order);
    }
}
