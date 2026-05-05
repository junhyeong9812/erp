package com.erp.procurement.application.port.inbound;

import com.erp.procurement.application.dto.command.IssuePurchaseOrderCommand;
import com.erp.procurement.application.dto.command.ReceiveGoodsCommand;

public interface PurchaseOrderUseCase {
    Long issuePurchaseOrder(IssuePurchaseOrderCommand command);
    void receiveGoods(ReceiveGoodsCommand command);
}