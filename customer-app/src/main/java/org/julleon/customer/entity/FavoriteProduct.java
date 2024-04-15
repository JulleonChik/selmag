package org.julleon.customer.entity;

import lombok.Data;

import java.util.UUID;


public record FavoriteProduct(UUID id, int productId) {
}
