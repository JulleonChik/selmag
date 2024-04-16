package org.julleon.feedback.repository;

import org.julleon.feedback.entity.FavoriteProduct;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface FavoriteProductRepository extends ReactiveCrudRepository<FavoriteProduct, UUID> {


    Mono<Void> deleteByUserIdAndProductId(String userId, int productId);

    Mono<FavoriteProduct> findByUserIdAndProductId(String userId, int productId);

    Flux<FavoriteProduct> findAllByUserId(String userId);
}
