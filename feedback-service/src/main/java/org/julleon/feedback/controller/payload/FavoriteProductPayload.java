package org.julleon.feedback.controller.payload;

import jakarta.validation.constraints.NotNull;

public record FavoriteProductPayload(
        @NotNull(message = "{feedback.payload.favorite_product.error.product_id.null}")
        Integer productId
) {
}
