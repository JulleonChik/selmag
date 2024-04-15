package org.julleon.customer.client;

import org.julleon.customer.entity.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductsClient {
    Flux<Product> getAllProductsWithFilter(String filter);

    Mono<Product> getProduct(String productId);
}
