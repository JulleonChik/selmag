package org.julleon.feedback.repository;

import org.julleon.feedback.entity.FavoriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoriteProductRepository {

    Mono<FavoriteProduct> save(FavoriteProduct favoriteProduct);

    Mono<Void> deleteByProductId(int productId);

    Mono<FavoriteProduct> findByProductId(int productId);

    Flux<FavoriteProduct> findAll();
}
