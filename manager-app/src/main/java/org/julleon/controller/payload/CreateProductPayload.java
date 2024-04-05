package org.julleon.controller.payload;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductPayload(
        @NotNull
        @Size(min = 3, max = 50, message = "{catalogue.errors.products.create.errors.title.size_is_invalid}")
        String title,

        @NotNull
        @Size(max = 1000, message = "{catalogue.errors.products.create.errors.description.size_is_invalid}")
        String description) {
}
