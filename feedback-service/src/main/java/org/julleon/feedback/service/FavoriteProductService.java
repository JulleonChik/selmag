package org.julleon.feedback.service;

import org.julleon.feedback.entity.FavoriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoriteProductService {

    Mono<FavoriteProduct> addProductToFavorites(String userId, int productId);

    Mono<Void> removeProductFromFavorites(String userId, int productId);

    Mono<FavoriteProduct> findFavoriteProductByUserAndProductId(String userId, int productId);

    Flux<FavoriteProduct> findAllFavoriteProductsByUserId(String userId);
}
