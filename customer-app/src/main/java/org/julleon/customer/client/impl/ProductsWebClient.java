package org.julleon.customer.client.impl;

import lombok.RequiredArgsConstructor;
import org.julleon.customer.client.ProductsClient;
import org.julleon.customer.entity.Product;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RequiredArgsConstructor
public class ProductsWebClient implements ProductsClient {

    private final WebClient webClient;


    @Override
    public Flux<Product> getAllProductsWithFilter(String filter) {
        return this.webClient
                .get()
                .uri("/catalogue-api/products?filter={filter}", filter)
                .retrieve()
                .bodyToFlux(Product.class);
    }

    @Override
    public Mono<Product> getProduct(int productId) {
        return this.webClient.get()
                .uri("/catalogue-api/products/{productId}", productId)
                .retrieve()
                .bodyToMono(Product.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }
}
