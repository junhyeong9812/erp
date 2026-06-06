package com.erp.promotion.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.promotion.application.port.outbound.PointRepository;
import com.erp.promotion.domain.entity.Point;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InMemoryPointRepository extends InMemoryRepository<Point, Long> implements PointRepository {
    @Override protected Long extractId(Point p) { return p.getId(); }
    @Override public List<Point> findActiveByCustomer(Long customerId) {
        return findAllBy(p -> p.getCustomerId().equals(customerId) && p.getStatus() == Point.Status.ACTIVE);
    }
}