package org.julleon.feedback.repository;

import org.julleon.feedback.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewRepository {

    Mono<ProductReview> save(ProductReview productPreview);

    Flux<ProductReview> findAllByProductId(int productId);
}
