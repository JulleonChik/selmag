package org.julleon.customer.controller;

import org.julleon.customer.client.FavoriteProductsClient;
import org.julleon.customer.client.ProductReviewsClient;
import org.julleon.customer.client.ProductsClient;
import org.julleon.customer.entity.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.mock.http.server.reactive.MockServerHttpResponse;
import org.springframework.ui.ConcurrentModel;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.NoSuchElementException;


@ExtendWith(MockitoExtension.class)
@DisplayName("ProductController: Модульные тесты")
class ProductControllerTest {
    @Mock
    ProductsClient productsClient;
    @Mock
    FavoriteProductsClient favoriteProductsClient;
    @Mock
    ProductReviewsClient productReviewsClient;

    @InjectMocks
    ProductController productController;


    @Test
    @DisplayName("обработка NoSuchElementException: добавление сообщения в атрибут модели и возвращение строки с адресом представления errors/404")
    void methodHandleNoSuchElementException_conditionNo_behaviourReturnsViewErrors404() {
//        given (начальное состояние необходимое для выполнения теста)
        NoSuchElementException noSuchElementException = new NoSuchElementException("Товар не найден");
        ConcurrentModel model = new ConcurrentModel();
        ServerHttpResponse serverHttpResponse = new MockServerHttpResponse();
//        when (осуществление тестируемого действия)
        String result = this.productController
                .handleNoSuchElementException(noSuchElementException, model, serverHttpResponse);
//        then (утверждения (assertions) - проверка ожидаемого результата или поведения)
        Assertions.assertEquals(HttpStatus.NOT_FOUND, serverHttpResponse.getStatusCode());
        Assertions.assertEquals("Товар не найден", model.getAttribute("error"));
        Assertions.assertEquals("errors/404", result);
    }


        @Test
        @DisplayName("возвращается MonoError с исключением NoSuchElementException, если MonoProduct полученный ProductsClient IsEmpty, ибо Product не существует")
        void methodGetProduct_conditionMonoProductRetrievedByProductsClientIsEmptyForDoesNotExists_behaviourReturnsMonoErrorWithNoSuchElementException() {
//        given (начальное состояние необходимое для выполнения теста)
        Mockito
                .doReturn(Mono.empty())
                .when(this.productsClient).getProduct(1);
//        when (осуществление тестируемого действия)
        Mono<Product> resultMonoProduct = this.productController.getProduct(1);
//        then (проверка ожидаемого результата или поведения)
        StepVerifier
                .create(resultMonoProduct)
                .expectErrorMatches(throwable ->
                        throwable instanceof NoSuchElementException &&
                        throwable.getMessage().equals("customer.products.error.product.not_found")
                )
                .verify();

        Mockito.verify(this.productsClient).getProduct(1);
        Mockito.verifyNoMoreInteractions(this.productsClient);

    }


    @Test
    void methodGetProduct_conditionMonoProductRetrievedByProductsClientNotEmpty_behaviourReturnsNotEmptyMonoProduct() {
//        given (начальное состояние необходимое для выполнения теста)
        Mockito
                .doReturn(Mono.just(new Product(1, "Товар №1", "Описание товара №1")))
                .when(this.productsClient).getProduct(1);
//        when (осуществление тестируемого действия)
        Mono<Product> resultMonoProduct = this.productController.getProduct(1);
//        then (проверка ожидаемого результата или поведения)
        StepVerifier
                .create(resultMonoProduct)
                .expectNext(new Product(1, "Товар №1", "Описание товара №1"))
                .expectComplete()
                .verify();

        Mockito.verify(this.productsClient).getProduct(1);
        Mockito.verifyNoMoreInteractions(this.productsClient);

    }


    @Test
    void methodRemoveProductFromFavorites_condition_behaviour() {
//        given (начальное состояние необходимое для выполнения теста)

//        when (осуществление тестируемого действия)

//        then (утверждения (assertions) - проверка ожидаемого результата или поведения)
    }

}