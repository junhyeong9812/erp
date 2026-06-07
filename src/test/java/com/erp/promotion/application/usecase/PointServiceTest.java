package com.erp.promotion.application.usecase;

import com.erp.common.domain.DomainEvent;
import com.erp.common.messaging.EventBus;
import com.erp.promotion.application.dto.command.EarnPointCommand;
import com.erp.promotion.application.dto.command.UsePointCommand;
import com.erp.promotion.application.port.outbound.PointRepository;
import com.erp.promotion.domain.entity.Point;
import com.erp.promotion.domain.event.PointEarnedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class PointServiceTest {

    /** Mock 대신 간단한 Fake Repository */
    static class FakePointRepo implements PointRepository {
        final ConcurrentHashMap<Long, Point> store = new ConcurrentHashMap<>();
        @Override public Point save(Point p) { store.put(p.getId(), p); return p; }
        @Override public Optional<Point> findById(Long id) { return Optional.ofNullable(store.get(id)); }
        @Override public List<Point> findActiveByCustomer(Long customerId) {
            return store.values().stream()
                    .filter(p -> p.getCustomerId().equals(customerId) && p.getStatus() == Point.Status.ACTIVE)
                    .collect(Collectors.toList());
        }
    }

    /** 수집용 EventBus */
    static class CapturingEventBus implements EventBus {
        final List<DomainEvent> published = new ArrayList<>();
        @Override public void publish(DomainEvent event) { published.add(event); }
        @Override public void publishAll(Iterable<? extends DomainEvent> events) {
            events.forEach(published::add);
        }
    }

    FakePointRepo repo;
    CapturingEventBus bus;
    PointService service;

    @BeforeEach
    void setUp() {
        repo = new FakePointRepo();
        bus = new CapturingEventBus();
        service = new PointService(repo, bus);
    }

    @Test
    void earn_은_저장하고_이벤트_발행() {
        Long id = service.earn(new EarnPointCommand(1L, 500, LocalDate.of(2030, 1, 1)));

        assertThat(id).isNotNull();
        assertThat(repo.findById(id)).isPresent();
        assertThat(bus.published).hasAtLeastOneElementOfType(PointEarnedEvent.class);
    }

    @Test
    void use_는_여러_Point_에_걸쳐_차감() {
        // 두 덩어리 적립
        service.earn(new EarnPointCommand(1L, 300, LocalDate.of(2030, 1, 1)));
        service.earn(new EarnPointCommand(1L, 500, LocalDate.of(2030, 1, 1)));

        int used = service.use(new UsePointCommand(1L, 600));

        assertThat(used).isEqualTo(600);
        int remaining = repo.findActiveByCustomer(1L).stream()
                .mapToInt(Point::getAmount).sum();
        assertThat(remaining).isEqualTo(200);
    }

    @Test
    void use_요청량이_보유량보다_많으면_보유량만_차감() {
        service.earn(new EarnPointCommand(1L, 100, LocalDate.of(2030, 1, 1)));

        int used = service.use(new UsePointCommand(1L, 500));

        assertThat(used).isEqualTo(100);
    }

    @Test
    void use_보유_포인트_없으면_0() {
        int used = service.use(new UsePointCommand(999L, 500));

        assertThat(used).isZero();
    }
}