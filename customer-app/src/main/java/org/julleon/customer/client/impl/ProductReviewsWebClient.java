package org.julleon.customer.client.impl;

import lombok.RequiredArgsConstructor;
import org.julleon.customer.client.ProductReviewsClient;
import org.julleon.customer.client.exception.ClientBadRequestException;
import org.julleon.customer.client.impl.payload.ProductReviewWebClientPayload;
import org.julleon.customer.entity.ProductReview;
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
public class ProductReviewsWebClient implements ProductReviewsClient {

    private final WebClient webClient;

    @Override
    public Mono<ProductReview> createProductReview(Integer productId, Integer rating, String review) {
        return this.webClient // Создание запроса с использованием WebClient
                .post() // HTTP POST запрос
                .uri("/feedback-api/product-reviews") // Установка URI
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new ProductReviewWebClientPayload(productId, rating, review)) // Формируем RequestBody
                .retrieve() // Инициирование запроса
                .bodyToMono(ProductReview.class) // Преобразование ответа в Mono<ProductReview>
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
    public Flux<ProductReview> findProductReviewsByProductId(Integer productId) {
        return this.webClient // Создание запроса с использованием WebClient
                .get() // HTTP GET запрос
                .uri("/feedback-api/product-reviews/by-product-id/{productId}", productId) // Установка URI с параметром productId
                .retrieve() // Инициирование запроса
                .bodyToFlux(ProductReview.class); // Преобразование ответа в Flux<ProductReview>
    }
}
