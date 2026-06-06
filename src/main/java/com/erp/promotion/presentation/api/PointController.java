package com.erp.promotion.presentation.api;

import com.erp.promotion.application.dto.command.EarnPointCommand;
import com.erp.promotion.application.port.inbound.PointUseCase;
import com.erp.promotion.presentation.dto.request.EarnPointRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/promotion/points")
public class PointController {

    private final PointUseCase useCase;

    public PointController(PointUseCase useCase) { this.useCase = useCase; }

    @PostMapping("/earn")
    public ResponseEntity<Long> earn(@RequestBody EarnPointRequest req) {
        return ResponseEntity.ok(useCase.earn(new EarnPointCommand(
                req.customerId(), req.amount(), LocalDate.parse(req.expireOn()))));
    }
}