package org.julleon.customer.service;

import org.julleon.customer.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewsService {

    Mono<ProductReview> createProductReview(int productId, int rating, String review);

    Flux<ProductReview> findProductReviewsByProductId(int productId);
}
