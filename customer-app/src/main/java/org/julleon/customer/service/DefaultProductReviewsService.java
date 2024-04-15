package org.julleon.customer.service;

import lombok.RequiredArgsConstructor;
import org.julleon.customer.entity.ProductReview;
import org.julleon.customer.repository.ProductReviewRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultProductReviewsService implements ProductReviewsService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    public Mono<ProductReview> createProductReview(int productId, int rating, String review) {
        return this.productReviewRepository
                .save(new ProductReview(UUID.randomUUID(), productId, rating, review));
    }

    @Override
    public Flux<ProductReview> findProductReviewsByProductId(int productId) {
        return productReviewRepository
                .findAllByProductId(productId);
    }
}
