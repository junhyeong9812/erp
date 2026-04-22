package com.erp.inventory.infrastructure.persistence;

import com.erp.common.persistence.InMemoryRepository;
import com.erp.common.support.IdGenerator;
import com.erp.inventory.application.port.outbound.ProductRepository;
import com.erp.inventory.domain.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class InMemoryProductRepository
        extends InMemoryRepository<Product, Long>
        implements ProductRepository {

    @Override
    protected Long extractId(Product entity) {
        return entity.getId();
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            product.assignId(IdGenerator.next());
        }
        return super.save(product);
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        return store.values().stream().filter(p -> p.getSku().equals(sku)).findFirst();
    }

    @Override
    public boolean existsBySku(String sku) {
        return findBySku(sku).isPresent();
    }
}