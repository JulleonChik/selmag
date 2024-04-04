package org.julleon.service;

import org.julleon.entity.Product;

import java.util.List;

public interface ProductService {
    List<Product> findAllProducts();

    Product createProduct(String title, String description);
}
