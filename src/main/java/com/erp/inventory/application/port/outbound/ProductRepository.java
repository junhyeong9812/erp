package com.erp.inventory.application.port.outbound;

import com.erp.inventory.domain.entity.Product;

import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    Optional<Product> findBySku(String sku);
    boolean existsBySku(String sku);
}