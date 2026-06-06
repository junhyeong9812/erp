package com.erp.promotion.application.port.outbound;

import com.erp.promotion.domain.entity.Point;

import java.util.List;
import java.util.Optional;

public interface PointRepository {
    Point save(Point point);
    Optional<Point> findById(Long id);
    List<Point> findActiveByCustomer(Long customerId);
}