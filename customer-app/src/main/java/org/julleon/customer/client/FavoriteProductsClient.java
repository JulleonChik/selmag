package org.julleon.customer.client;

import org.julleon.customer.entity.FavoriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoriteProductsClient {
    Mono<FavoriteProduct> addProductToFavorites(Integer productId);

    Mono<FavoriteProduct> findFavoriteProductByProductId(Integer productId);

    Mono<Void> removeProductFromFavorites(Integer productId);

    Flux<FavoriteProduct> findAllFavoriteProducts();

}
