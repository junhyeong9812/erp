package com.erp.settlement.infrastructure.batch;

import com.erp.settlement.infrastructure.batch.port.SellerRegistryRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@StepScope
public class ActiveSellerReader implements ItemReader<Long> {

    private final Iterator<Long> iterator;

    public ActiveSellerReader(SellerRegistryRepository registry) {
        this.iterator = registry.findActiveSellerIds().iterator();
    }

    @Override
    public Long read() {
        return iterator.hasNext() ? iterator.next() : null;
    }
}
