package com.erp.promotion.application.usecase;

import com.erp.common.messaging.EventBus;
import com.erp.common.support.IdGenerator;
import com.erp.promotion.application.dto.command.EarnPointCommand;
import com.erp.promotion.application.dto.command.UsePointCommand;
import com.erp.promotion.application.port.inbound.PointUseCase;
import com.erp.promotion.application.port.outbound.PointRepository;
import com.erp.promotion.domain.entity.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class PointService implements PointUseCase {

    private final PointRepository repo;
    private final EventBus eventBus;

    public PointService(PointRepository repo, EventBus eventBus) {
        this.repo = repo;
        this.eventBus = eventBus;
    }

    @Override
    public Long earn(EarnPointCommand cmd) {
        Point p = Point.earn(cmd.customerId(), cmd.amount(), cmd.expireOn());
        p.assignId(IdGenerator.next());
        repo.save(p);
        eventBus.publishAll(p.pullEvents());
        return p.getId();
    }

    @Override
    public int use(UsePointCommand cmd) {
        List<Point> points = repo.findActiveByCustomer(cmd.customerId());
        int remaining = cmd.request();
        for (Point p : points) {
            if (remaining == 0) break;
            int used = p.consume(remaining);
            remaining -= used;
            repo.save(p);
        }
        return cmd.request() - remaining;
    }

    @Override
    public void expireOutdated() {
        LocalDate today = LocalDate.now();
        // 실제로는 전체 스캔 대신 인덱스 쿼리
        repo.findActiveByCustomer(-1L);  // placeholder
        // 간략화: 각 모듈에서 구체 구현
    }
}