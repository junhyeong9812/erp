package com.erp.promotion.application.port.inbound;

import com.erp.promotion.application.dto.command.EarnPointCommand;
import com.erp.promotion.application.dto.command.UsePointCommand;

public interface PointUseCase {
    Long earn(EarnPointCommand command);
    int use(UsePointCommand command);
    void expireOutdated();
}