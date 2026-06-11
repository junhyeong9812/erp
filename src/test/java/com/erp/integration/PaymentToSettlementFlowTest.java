package com.erp.integration;

import com.erp.payment.application.dto.command.RequestPaymentCommand;
import com.erp.payment.application.port.inbound.PaymentUseCase;
import com.erp.settlement.application.port.outbound.LedgerRepository;
import com.erp.settlement.domain.entity.Ledger;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PaymentToSettlementFlowTest {

    @Autowired private PaymentUseCase paymentUseCase;
    @Autowired private LedgerRepository ledgerRepository;

    @Test
    void 결제_완료_시_매출_전표_자동_생성() {
        paymentUseCase.requestPayment(new RequestPaymentCommand(1L, "CARD", 5000));

        Awaitility.await()
                .atMost(Duration.ofSeconds(2))
                .untilAsserted(() -> {
                    var sales = ledgerRepository.findByPeriodAndType(1L, Ledger.Type.SALES);
                    assertThat(sales).isNotEmpty();
                    assertThat(sales.get(0).getCredit().amount().longValueExact()).isEqualTo(5000);
                });
    }
}