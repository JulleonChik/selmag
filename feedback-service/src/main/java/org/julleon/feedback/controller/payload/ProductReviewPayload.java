package org.julleon.feedback.controller.payload;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductReviewPayload(
        @NotNull(message = "{feedback.payload.product_review.error.product_id.null}")
        Integer productId,
        @NotNull(message = "{feedback.payload.product_review.error.rating.null}")
        @Min(value = 1, message = "{feedback.payload.product_review.error.rating.min}")
        @Max(value = 5, message = "{feedback.payload.product_review.error.rating.max}")
        Integer rating,
        @Size(max = 1000, message = "{feedback.payload.product_review.error.review.size}")
        String review
) {
}
