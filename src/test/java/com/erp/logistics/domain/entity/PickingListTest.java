package com.erp.logistics.domain.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PickingListTest {

    @Test
    void of_로_생성하면_지정된_값_보유() {
        PickingList p = PickingList.of(100L, 200L, 5, "A-01-02");
        p.assignId(1L);

        assertThat(p.getId()).isEqualTo(1L);
    }
}