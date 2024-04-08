package org.julleon.catalogue.service;


import org.julleon.catalogue.controller.payload.CreateProductPayload;
import org.julleon.catalogue.controller.payload.UpdateProductPayload;
import org.julleon.catalogue.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductService {
    Iterable<Product> findAllProducts();

    Iterable<Product> findProductsByTitlePart(String partTitle);

    Product createProduct(CreateProductPayload createProductPayload);

    Optional<Product> findProductById(int productId);

    void updateProduct(int productId, UpdateProductPayload payload);

    void deleteProduct(int productId);
}
