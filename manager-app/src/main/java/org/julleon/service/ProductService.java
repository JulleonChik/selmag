package org.julleon.service;

import org.julleon.controller.payload.CreateProductPayload;
import org.julleon.controller.payload.UpdateProductPayload;
import org.julleon.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    List<Product> findAllProducts();

    Product createProduct(CreateProductPayload createProductPayload);

    Optional<Product> findProductById(int productId);

    void updateProduct(int productId, UpdateProductPayload payload);

    void deleteProduct(int productId);
}
