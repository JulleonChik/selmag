package org.julleon.feedback.service.impl;

import lombok.RequiredArgsConstructor;
import org.julleon.feedback.entity.FavoriteProduct;
import org.julleon.feedback.repository.FavoriteProductRepository;
import org.julleon.feedback.service.FavoriteProductService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DefaultFavoriteProductService implements FavoriteProductService {

    private final FavoriteProductRepository favoriteProductRepository;


    @Override
    public Mono<FavoriteProduct> addProductToFavorites(String userId, int productId) {
        return this.favoriteProductRepository.save(new FavoriteProduct(UUID.randomUUID(), userId, productId));
    }

    @Override
    public Mono<Void> removeProductFromFavorites(String userId, int productId) {
        return this.favoriteProductRepository.deleteByUserIdAndProductId(userId, productId);
    }

    @Override
    public Mono<FavoriteProduct> findFavoriteProductByUserAndProductId(String userId, int productId) {
        return this.favoriteProductRepository.findByUserIdAndProductId(userId, productId);
    }

    @Override
    public Flux<FavoriteProduct> findAllFavoriteProductsByUserId(String userId) {
        return this.favoriteProductRepository.findAllByUserId(userId);
    }
}
