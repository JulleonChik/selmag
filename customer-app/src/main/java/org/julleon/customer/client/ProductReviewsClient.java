package org.julleon.customer.client;

import org.julleon.customer.entity.ProductReview;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductReviewsClient {

    Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review);

    Flux<ProductReview> findProductReviewsByProductId(Integer productId);
}
