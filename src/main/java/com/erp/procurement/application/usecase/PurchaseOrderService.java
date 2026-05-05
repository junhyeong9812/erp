package com.erp.procurement.application.usecase;

import com.erp.common.domain.Money;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.procurement.application.dto.command.IssuePurchaseOrderCommand;
import com.erp.procurement.application.dto.command.ReceiveGoodsCommand;
import com.erp.procurement.application.port.inbound.PurchaseOrderUseCase;
import com.erp.procurement.application.port.outbound.PurchaseOrderRepository;
import com.erp.procurement.domain.entity.PurchaseOrder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PurchaseOrderService implements PurchaseOrderUseCase {

    private final PurchaseOrderRepository repository;
    private final EventBus eventBus;

    public PurchaseOrderService(PurchaseOrderRepository repository, EventBus eventBus) {
        this.repository = repository;
        this.eventBus = eventBus;
    }

    @Override
    public Long issuePurchaseOrder(IssuePurchaseOrderCommand cmd) {
        PurchaseOrder po = PurchaseOrder.issue(cmd.supplierId(), cmd.productId(), cmd.quantity(), Money.of(cmd.unitPrice()));
        po.assignId(IdGenerator.next());
        repository.save(po);
        eventBus.publishAll(po.pullEvents());
        return po.getId();
    }

    @Override
    public void receiveGoods(ReceiveGoodsCommand cmd) {
        PurchaseOrder po = repository.findById(cmd.purchaseOrderId()).orElseThrow(NotFoundException::new);
        po.receive(cmd.quantity());
        repository.save(po);
        eventBus.publishAll(po.pullEvents());
    }
}