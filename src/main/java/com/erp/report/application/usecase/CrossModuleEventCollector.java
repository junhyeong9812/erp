package com.erp.report.application.usecase;

import com.erp.common.support.IdGenerator;
import com.erp.payment.domain.event.PaymentCompletedEvent;
import com.erp.report.application.port.outbound.MetricRepository;
import com.erp.report.domain.entity.Metric;
import com.erp.sales.domain.event.SalesOrderPlacedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
public class CrossModuleEventCollector {

    private final MetricRepository metricRepository;

    public CrossModuleEventCollector(MetricRepository metricRepository) {
        this.metricRepository = metricRepository;
    }

    @ApplicationModuleListener
    public void onPayment(PaymentCompletedEvent event) {
        Metric m = Metric.of("payment.amount", "order:" + event.orderId(), event.amount());
        m.assignId(IdGenerator.next());
        metricRepository.save(m);
    }

    @ApplicationModuleListener
    public void onOrderPlaced(SalesOrderPlacedEvent event) {
        int totalQty = event.lines().stream().mapToInt(SalesOrderPlacedEvent.Line::quantity).sum();
        Metric m = Metric.of("sales.order.quantity", "customer:" + event.customerId(), totalQty);
        m.assignId(IdGenerator.next());
        metricRepository.save(m);
    }
}