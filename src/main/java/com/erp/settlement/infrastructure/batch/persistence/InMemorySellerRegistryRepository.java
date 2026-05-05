package com.erp.settlement.infrastructure.batch.persistence;

import com.erp.settlement.infrastructure.batch.port.SellerRegistryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemorySellerRegistryRepository implements SellerRegistryRepository {

    private final Set<Long> activeIds = ConcurrentHashMap.newKeySet();

    @Override
    public void register(Long sellerId) {
        activeIds.add(sellerId);
    }

    @Override
    public void deregister(Long sellerId) {
        activeIds.remove(sellerId);
    }

    @Override
    public List<Long> findActiveSellerIds() {
        return activeIds.stream().sorted().toList();
    }
}
