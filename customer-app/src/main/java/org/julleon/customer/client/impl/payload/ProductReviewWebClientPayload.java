package org.julleon.customer.client.impl.payload;

public record ProductReviewWebClientPayload(
        Integer productId,
        Integer rating,
        String review
) {
}
