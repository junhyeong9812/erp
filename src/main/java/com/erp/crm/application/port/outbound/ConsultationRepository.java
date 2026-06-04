package com.erp.crm.application.port.outbound;

import com.erp.crm.domain.entity.Consultation;

public interface ConsultationRepository {
    Consultation save(Consultation consultation);
}