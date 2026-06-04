package com.erp.crm.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.crm.application.port.outbound.ConsultationRepository;
import com.erp.crm.domain.entity.Consultation;
import org.springframework.stereotype.Repository;


@Repository
public class InMemoryConsultationRepository extends InMemoryRepository<Consultation, Long>
        implements ConsultationRepository {
    @Override protected Long extractId(Consultation c) { return c.getId(); }
}