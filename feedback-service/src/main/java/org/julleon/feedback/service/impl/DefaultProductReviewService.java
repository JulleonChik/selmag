package org.julleon.feedback.service.impl;

import lombok.RequiredArgsConstructor;
import org.julleon.feedback.entity.ProductReview;
import org.julleon.feedback.repository.ProductReviewRepository;
import org.julleon.feedback.service.ProductReviewService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Service
@RequiredArgsConstructor
public class DefaultProductReviewService implements ProductReviewService {

    private final ProductReviewRepository productReviewRepository;

    @Override
    public Mono<ProductReview> createProductReview(int productId, int rating, String review, String userId) {
        return this.productReviewRepository.save(
                new ProductReview(UUID.randomUUID(), userId, productId, rating, review));
    }

    @Override
    public Flux<ProductReview> findProductReviewsByProductId(int productId) {
        return this.productReviewRepository.findAllByProductId(productId);
    }
}
