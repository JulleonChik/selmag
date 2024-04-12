package org.julleon.catalogue.controller.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProductPayload(
        @NotBlank(message = "{catalogue.errors.products.create.title.is_blank}")
        @Size(min = 3, max = 50, message = "{catalogue.errors.products.create.title.size_is_invalid}")
        String title,

        @NotBlank(message = "{catalogue.errors.products.create.description.is_blank}")
        @Size(max = 1000, message = "{catalogue.errors.products.create.description.size_is_invalid}")
        String description) {
}
