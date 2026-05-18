package com.erp.production.application.usecase;

import com.erp.production.application.port.outbound.BomRepository;
import com.erp.production.domain.entity.BillOfMaterials;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MaterialRequirementPlannerTest {

    @Test
    void 정책이_없으면_기본_0_재고로_간주하고_예외없이_종료() {
        BomRepository bomRepository = mock(BomRepository.class);
        when(bomRepository.findByFinishedProductId(100L))
                .thenReturn(Optional.of(BillOfMaterials.of(100L, List.of(
                        new BillOfMaterials.Component(200L, 2)
                ))));

        @SuppressWarnings("unchecked")
        ObjectProvider<MaterialRequirementPlanner.StockQueryPolicy> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable(any(Supplier.class)))
                .thenAnswer(inv -> ((Supplier<MaterialRequirementPlanner.StockQueryPolicy>) inv.getArgument(0)).get());

        MaterialRequirementPlanner planner = new MaterialRequirementPlanner(bomRepository, provider);

        // 예외 없이 끝나야 함
        planner.plan(100L, 10);

        verify(bomRepository).findByFinishedProductId(100L);
    }

    @Test
    void 정책이_있으면_재고조회_후_부족분만_처리() {
        BomRepository bomRepository = mock(BomRepository.class);
        when(bomRepository.findByFinishedProductId(100L))
                .thenReturn(Optional.of(BillOfMaterials.of(100L, List.of(
                        new BillOfMaterials.Component(200L, 2),
                        new BillOfMaterials.Component(201L, 3)
                ))));

        MaterialRequirementPlanner.StockQueryPolicy policy = mock(MaterialRequirementPlanner.StockQueryPolicy.class);
        // 200L 은 충분, 201L 은 부족
        when(policy.availableStock(200L)).thenReturn(100);
        when(policy.availableStock(201L)).thenReturn(5);

        @SuppressWarnings("unchecked")
        ObjectProvider<MaterialRequirementPlanner.StockQueryPolicy> provider = mock(ObjectProvider.class);
        when(provider.getIfAvailable(any(Supplier.class))).thenReturn(policy);

        MaterialRequirementPlanner planner = new MaterialRequirementPlanner(bomRepository, provider);

        planner.plan(100L, 10);  // 요구: 200L=20, 201L=30

        verify(policy).availableStock(200L);
        verify(policy).availableStock(201L);
    }

    @Test
    void BOM_없으면_NoSuchElementException() {
        BomRepository bomRepository = mock(BomRepository.class);
        when(bomRepository.findByFinishedProductId(999L)).thenReturn(Optional.empty());
        @SuppressWarnings("unchecked")
        ObjectProvider<MaterialRequirementPlanner.StockQueryPolicy> provider = mock(ObjectProvider.class);

        MaterialRequirementPlanner planner = new MaterialRequirementPlanner(bomRepository, provider);

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> planner.plan(999L, 1))
                .isInstanceOf(java.util.NoSuchElementException.class);
    }
}