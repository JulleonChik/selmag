package org.julleon.customer.controller.payload;

public record ProductReviewPayload(
        Integer rating,
        String review
) {
}
