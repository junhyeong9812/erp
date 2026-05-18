package com.erp.production.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BillOfMaterialsTest {

    @Test
    void of_로_생성하면_finishedProductId_와_components_가_세팅() {
        BillOfMaterials bom = BillOfMaterials.of(100L, List.of(
                new BillOfMaterials.Component(200L, 2),
                new BillOfMaterials.Component(201L, 3)
        ));

        assertThat(bom.getFinishedProductId()).isEqualTo(100L);
        assertThat(bom.getComponents()).hasSize(2);
    }

    @Test
    void requirementsFor_10_은_각_부품_수량_10배() {
        BillOfMaterials bom = BillOfMaterials.of(100L, List.of(
                new BillOfMaterials.Component(200L, 2),
                new BillOfMaterials.Component(201L, 3)
        ));

        List<BillOfMaterials.Component> req = bom.requirementsFor(10);

        assertThat(req).extracting(BillOfMaterials.Component::componentProductId)
                .containsExactly(200L, 201L);
        assertThat(req).extracting(BillOfMaterials.Component::quantityPerUnit)
                .containsExactly(20, 30);
    }

    @Test
    void requirementsFor_0_이면_모든_소요량_0() {
        BillOfMaterials bom = BillOfMaterials.of(100L, List.of(
                new BillOfMaterials.Component(200L, 2),
                new BillOfMaterials.Component(201L, 3)
        ));

        List<BillOfMaterials.Component> req = bom.requirementsFor(0);

        assertThat(req).extracting(BillOfMaterials.Component::quantityPerUnit)
                .containsExactly(0, 0);
    }

    @Test
    void requirementsFor_는_새_리스트_반환하여_원본_불변() {
        BillOfMaterials bom = BillOfMaterials.of(100L, List.of(
                new BillOfMaterials.Component(200L, 2)
        ));

        List<BillOfMaterials.Component> req = bom.requirementsFor(5);

        assertThat(req).isNotSameAs(bom.getComponents());
        // 원본 components 는 그대로 2
        assertThat(bom.getComponents().get(0).quantityPerUnit()).isEqualTo(2);
    }

    @Test
    void getComponents_는_unmodifiable_View() {
        BillOfMaterials bom = BillOfMaterials.of(100L, List.of(
                new BillOfMaterials.Component(200L, 2)
        ));

        assertThatThrownBy(() -> bom.getComponents()
                .add(new BillOfMaterials.Component(999L, 1)))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}