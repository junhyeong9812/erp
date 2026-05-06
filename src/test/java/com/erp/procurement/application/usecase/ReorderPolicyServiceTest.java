package com.erp.procurement.application.usecase;

import com.erp.procurement.application.dto.command.RegisterReorderPolicyCommand;
import com.erp.procurement.application.port.outbound.ReorderPolicyRepository;
import com.erp.procurement.domain.entity.ReorderPolicy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReorderPolicyServiceTest {

    @Mock ReorderPolicyRepository repository;
    @InjectMocks ReorderPolicyService service;

    @Test
    void 새_productId_정책은_정상_등록() {
        when(repository.findByProductId(100L)).thenReturn(Optional.empty());

        Long id = service.registerReorderPolicy(
                new RegisterReorderPolicyCommand(100L, 7L, 200));

        assertThat(id).isNotNull();
        ArgumentCaptor<ReorderPolicy> cap = ArgumentCaptor.forClass(ReorderPolicy.class);
        verify(repository).save(cap.capture());
        ReorderPolicy saved = cap.getValue();
        assertThat(saved.getProductId()).isEqualTo(100L);
        assertThat(saved.getDefaultSupplierId()).isEqualTo(7L);
        assertThat(saved.getReorderQuantity()).isEqualTo(200);
    }

    @Test
    void 같은_productId_중복_등록은_거부() {
        ReorderPolicy existing = ReorderPolicy.of(100L, 9L, 50);
        existing.assignId(1L);
        when(repository.findByProductId(100L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.registerReorderPolicy(
                new RegisterReorderPolicyCommand(100L, 7L, 200)))
                .isInstanceOf(IllegalStateException.class);

        verify(repository, never()).save(org.mockito.ArgumentMatchers.any());
    }
}