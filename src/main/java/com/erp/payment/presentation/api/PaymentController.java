package com.erp.payment.presentation.api;

import com.erp.payment.application.dto.command.RefundCommand;
import com.erp.payment.application.dto.command.RequestPaymentCommand;
import com.erp.payment.application.port.inbound.PaymentUseCase;
import com.erp.payment.presentation.dto.request.PaymentRequest;
import com.erp.payment.presentation.dto.response.PaymentResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentUseCase paymentUseCase;

    public PaymentController(PaymentUseCase paymentUseCase) {
        this.paymentUseCase = paymentUseCase;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> request(@RequestBody PaymentRequest req) {
        Long id = paymentUseCase.requestPayment(
                new RequestPaymentCommand(req.orderId(), req.method(), req.amount()));
        return ResponseEntity.ok(new PaymentResponse(id, req.orderId(), req.amount(), "COMPLETED"));
    }

    @PostMapping("/{paymentId}/refund")
    public ResponseEntity<Long> refund(
            @PathVariable Long paymentId,
            @RequestBody PaymentRequest req
    ) {
        Long id = paymentUseCase.refund(
                new RefundCommand(paymentId, req.orderId(), req.amount(), "API"));
        return ResponseEntity.ok(id);
    }
}