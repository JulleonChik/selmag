package org.julleon.repository;

import org.julleon.entity.Product;

import java.util.List;

public interface ProductRepository {
    List<Product> findAll();

    Product save(Product product);
}
