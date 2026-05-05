package com.erp.settlement.infrastructure.batch.port;

import java.util.List;

public interface SellerRegistryRepository {
    List<Long> findActiveSellerIds();
    void register(Long sellerId);
    void deregister(Long sellerId);
}
