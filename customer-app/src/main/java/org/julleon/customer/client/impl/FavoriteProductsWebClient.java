package org.julleon.customer.client.impl;

import lombok.RequiredArgsConstructor;
import org.julleon.customer.client.FavoriteProductsClient;
import org.julleon.customer.client.exception.ClientBadRequestException;
import org.julleon.customer.client.impl.payload.FavoriteProductWebClientPayload;
import org.julleon.customer.entity.FavoriteProduct;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
public class FavoriteProductsWebClient implements FavoriteProductsClient {
    private final WebClient webClient;

    @Override
    public Mono<FavoriteProduct> addProductToFavorites(Integer productId) {
        return this.webClient
                .post()
                .uri("/feedback-api/favorite-products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new FavoriteProductWebClientPayload(productId))
                .retrieve()
                .bodyToMono(FavoriteProduct.class)
                .onErrorMap(WebClientResponseException.BadRequest.class, webClientResponseException -> {
                    ProblemDetail problemDetail = webClientResponseException.getResponseBodyAs(ProblemDetail.class); // Получение деталей проблемы из исключения
                    List<String> errors = Optional.ofNullable(problemDetail)  // Получение problemDetail, если он не null
                            .map(ProblemDetail::getProperties) // Получение свойств проблемы
                            .map(properties -> properties.get("errors")) // Получение списка ошибок из свойств
                            .filter(List.class::isInstance)  // Фильтрация, чтобы убедиться, что это список
                            .map(List.class::cast) // Приведение к типу List<String>
                            .orElse(Collections.emptyList()); // Если список ошибок не определен, вернуть пустой список
                    // Бросаем кастомное исключение ClientBadRequestException с передачей ошибок
                    return new ClientBadRequestException(webClientResponseException, errors);
                });
    }

    @Override
    public Mono<FavoriteProduct> findFavoriteProductByProductId(Integer productId) {
        return this.webClient
                .get()
                .uri("/feedback-api/favorite-products/by-product-id/{productId}", productId)
                .retrieve()
                .bodyToMono(FavoriteProduct.class)
                .onErrorComplete(WebClientResponseException.NotFound.class);
    }

    @Override
    public Mono<Void> removeProductFromFavorites(Integer productId) {
        return this.webClient
                .delete()
                .uri("/feedback-api/favorite-products/by-product-id/{productId}", productId)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    @Override
    public Flux<FavoriteProduct> findAllFavoriteProducts() {
        return this.webClient
                .get()
                .uri("/feedback-api/favorite-products")
                .retrieve()
                .bodyToFlux(FavoriteProduct.class);
    }
}
