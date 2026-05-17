package com.erp.production.application.usecase;

import com.erp.production.application.port.outbound.BomRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

/**
 * ObjectProvider 로 Lazy 주입. 테스트 시 정책 미설정 상태에서도 서비스 구동 가능.
 */
@Service
public class MaterialRequirementPlanner {

    private final BomRepository bomRepository;
    private final ObjectProvider<StockQueryPolicy> stockQueryPolicy;  // 외부 모듈 정책
    // 정책 미설정 = 재고 정보 부재 → 안전을 위해 필요량 전체를 발주 대상으로 기록
    private static final StockQueryPolicy ASSUME_NO_STOCK = id -> 0;

    public MaterialRequirementPlanner(BomRepository bomRepository,
                                      ObjectProvider<StockQueryPolicy> stockQueryPolicy) {
        this.bomRepository = bomRepository;
        this.stockQueryPolicy = stockQueryPolicy;
    }

    public void plan(Long finishedProductId, int quantity) {
        var bom = bomRepository.findByFinishedProductId(finishedProductId).orElseThrow();
        var requirements = bom.requirementsFor(quantity);
        // 재고 조회 정책 미등록 시: 재고 0 가정 → 필요량 전체를 발주 후보로 기록
        StockQueryPolicy policy = stockQueryPolicy.getIfAvailable(() -> ASSUME_NO_STOCK);

        requirements.forEach(req -> {
            int onHand = policy.availableStock(req.componentProductId());
            int shortage = req.quantityPerUnit() - onHand;
            if (shortage > 0) {
                // 발주 로직 (외부 Procurement 모듈과 연동) — 실제로는 이벤트 또는 포트 호출
            }
        });
    }

    @FunctionalInterface
    public interface StockQueryPolicy {
        int availableStock(Long productId);
    }
}