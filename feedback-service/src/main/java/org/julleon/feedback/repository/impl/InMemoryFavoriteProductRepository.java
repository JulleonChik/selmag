package org.julleon.feedback.repository.impl;

import org.julleon.feedback.entity.FavoriteProduct;
import org.julleon.feedback.repository.FavoriteProductRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Repository
public class InMemoryFavoriteProductRepository implements FavoriteProductRepository {

    private final List<FavoriteProduct> favoriteProducts = Collections.synchronizedList(new LinkedList<>());

    @Override
    public Mono<FavoriteProduct> save(FavoriteProduct favoriteProduct) {
        this.favoriteProducts.add(favoriteProduct);
        return Mono.just(favoriteProduct);
    }

    @Override
    public Mono<Void> deleteByProductId(int productId) {
        this.favoriteProducts
                .removeIf(favoriteProduct -> Objects.equals(favoriteProduct.getProductId(), productId));
        return Mono.empty();
    }

    @Override
    public Mono<FavoriteProduct> findByProductId(int productId) {
        return Flux.fromIterable(this.favoriteProducts)
                .filter(favoriteProduct -> favoriteProduct.getProductId() == productId)
                .singleOrEmpty();
    }

    @Override
    public Flux<FavoriteProduct> findAll() {
        return Flux.fromIterable(this.favoriteProducts);
    }
}
