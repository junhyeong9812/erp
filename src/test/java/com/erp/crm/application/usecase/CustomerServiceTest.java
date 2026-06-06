package com.erp.crm.application.usecase;

import com.erp.common.domain.DomainEvent;
import com.erp.common.exception.NotFoundException;
import com.erp.common.messaging.EventBus;
import com.erp.crm.application.dto.command.RegisterCustomerCommand;
import com.erp.crm.application.port.outbound.CustomerRepository;
import com.erp.crm.domain.entity.Customer;
import com.erp.crm.domain.event.CustomerGradeChangedEvent;
import com.erp.crm.domain.event.CustomerRegisteredEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerRepository repo;
    private EventBus eventBus;
    private CustomerService service;

    @BeforeEach
    void setUp() {
        repo = mock(CustomerRepository.class);
        eventBus = mock(EventBus.class);
        service = new CustomerService(repo, eventBus);
    }

    @Test
    void register_는_저장_후_CustomerRegisteredEvent_발행() {
        Long id = service.register(new RegisterCustomerCommand(
                "C001", "ACME", "-", 1L, 1_000_000L));

        assertThat(id).isNotNull();
        verify(repo).save(any(Customer.class));
        verify(eventBus).publishAll(argThat((List<DomainEvent> evts) ->
                evts.stream().anyMatch(e -> e instanceof CustomerRegisteredEvent)));
    }

    @Test
    void recordPurchase_없는_고객은_NotFoundException() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.recordPurchase(999L, 100_000L))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).save(any());
        verify(eventBus, never()).publishAll(any());
    }

    @Test
    void recordPurchase_가_등급_전환하면_CustomerGradeChangedEvent_발행() {
        Customer c = Customer.register("C001", "ACME", "-", 1L,
                com.erp.common.domain.Money.of(20_000_000));
        c.assignId(1L);
        c.pullEvents(); // 등록 이벤트 클리어
        when(repo.findById(1L)).thenReturn(Optional.of(c));

        service.recordPurchase(1L, 1_000_000L); // NORMAL → SILVER

        verify(repo).save(c);
        verify(eventBus).publishAll(argThat((List<DomainEvent> evts) ->
                evts.stream().anyMatch(e -> e instanceof CustomerGradeChangedEvent)));
    }

    @Test
    void recordPurchase_가_등급_미전환이면_GradeChanged_미발행() {
        Customer c = Customer.register("C001", "ACME", "-", 1L,
                com.erp.common.domain.Money.of(20_000_000));
        c.assignId(1L);
        c.pullEvents();
        when(repo.findById(1L)).thenReturn(Optional.of(c));

        service.recordPurchase(1L, 500_000L); // NORMAL 유지

        verify(repo).save(c);
        // publishAll 은 빈 리스트여도 호출됨 - GradeChanged 가 없는지만 검증
        verify(eventBus).publishAll(argThat((List<DomainEvent> evts) ->
                evts.stream().noneMatch(e -> e instanceof CustomerGradeChangedEvent)));
    }
}