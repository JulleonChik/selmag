package org.julleon.manager.client;

import org.julleon.manager.controller.payload.CreateProductPayload;
import org.julleon.manager.controller.payload.UpdateProductPayload;
import org.julleon.manager.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductsRestClient {
    List<Product> findAllProducts(String filter);

    Product createProduct(CreateProductPayload createProductPayload);

    Optional<Product> findProductById(int productId);

    void updateProduct(int productId, UpdateProductPayload updateProductPayload);

    void deleteProductById(int productId);
}
