package org.julleon.customer.service;


import org.julleon.customer.entity.FavoriteProduct;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FavoriteProductsService {

    Mono<FavoriteProduct> addProductToFavorites(int productId);

    Mono<Void> removeProductFromFavorites(int productId);

    Mono<FavoriteProduct> findFavoriteProductByProductId(int productId);

    Flux<FavoriteProduct> findAllFavoriteProducts();
}
