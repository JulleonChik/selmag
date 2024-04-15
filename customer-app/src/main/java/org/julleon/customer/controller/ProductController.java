package org.julleon.customer.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.julleon.customer.client.FavoriteProductsClient;
import org.julleon.customer.client.ProductReviewsClient;
import org.julleon.customer.client.ProductsClient;
import org.julleon.customer.client.exception.ClientBadRequestException;
import org.julleon.customer.controller.payload.ProductReviewPayload;
import org.julleon.customer.entity.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Slf4j
@Controller
@RequestMapping("customer/products/{productId:\\d+}")
@RequiredArgsConstructor
public class ProductController {

    private final ProductsClient productsClient;

    private final FavoriteProductsClient favoriteProductsClient;

    private final ProductReviewsClient productReviewsClient;


    @ModelAttribute(name = "product", binding = false)
    public Mono<Product> getProduct(
            @PathVariable("productId") String productId
    ) {
        return this.productsClient.getProduct(productId)
                .switchIfEmpty(Mono.error(new NoSuchElementException("customer.products.error.product.not_found")));
    }


    @GetMapping
    public Mono<String> getPageWithProduct(
            @ModelAttribute("product") Mono<Product> productMono,
            Model model
    ) {
        return productMono // Получаем Mono<Product> из модели
                .map(Product::id) // Преобразуем Product в его идентификатор
                .flatMap(productId ->  // Выполняем операции для каждого идентификатора
                        Mono.zip( // Объединяем результаты двух асинхронных операций в один объект
                                this.productReviewsClient.findProductReviewsByProductId(productId).collectList(), // Получаем список отзывов о продукте
                                this.favoriteProductsClient.findFavoriteProductByProductId(productId).hasElement()// Проверяем, добавлен ли продукт в избранное
                        ).doOnNext(tuple -> { // Выполняем действия при получении результата
                            model.addAttribute("reviews", tuple.getT1()); // Добавляем список отзывов в модель
                            model.addAttribute("inFavorites", tuple.getT2()); // Добавляем флаг наличия продукта в избранном в модель
                        })
                )
                .thenReturn("customer/products/product"); // Возвращаем имя представления после завершения операций
    }


    @PostMapping("create-review") // Обработчик POST запроса для создания отзыва о продукте
    public Mono<String> createReview(
            Model model,
            @ModelAttribute("product") Mono<Product> productMono, // Получение Mono<Product> из модели
            ProductReviewPayload productReviewPayload
    ) {
        return productMono // Возвращаемый результат - Mono<String>, представляющий URL для перенаправления
                .flatMap(product -> this.productReviewsClient.createProductReview(product.id(), productReviewPayload.rating(), productReviewPayload.review()) // Вызов flatMap для выполнения асинхронной операции создания отзыва о продукте
                        .thenReturn("redirect:/customer/products/%d".formatted(product.id()))) // Возврат строки с перенаправлением на страницу продукта после успешного создания отзыва о продукте
                .onErrorResume(ClientBadRequestException.class, exception -> this.getPageWithProduct(productMono, model)
                        .doOnNext(viewName -> {
                            model.addAttribute("productReviewPayload", productReviewPayload);
                            model.addAttribute("errors", exception.getErrors());
                        }));
    }

    @PostMapping("add-to-favorites") // Обработчик POST запроса для добавления продукта в избранное
    public Mono<String> addProductToFavorites(
            @ModelAttribute("product") Mono<Product> productMono // Получение Mono<Product> из модели
    ) {
        return productMono
                .flatMap(product -> // Преобразование Mono<Product> в Product и выполнение операций для каждого продукта
                        this.favoriteProductsClient.addProductToFavorites(product.id()) // Добавление продукта в избранное
                                .thenReturn("redirect:/customer/products/%d".formatted(product.id()))
                                .onErrorResume(ClientBadRequestException.class, exception -> {
                                            log.error(exception.getMessage(), exception);
                                            return Mono.just("redirect:/customer/products/%d".formatted(product.id()));
                                        }
                                ));
    }


    @PostMapping("remove-from-favorites") // Обработчик POST запроса для удаления продукта из избранного
    public Mono<String> removeProductFromFavorites(
            @ModelAttribute("product") Mono<Product> productMono // Получение Mono<Product> из модели
    ) {
        return productMono// Возвращаемый результат - Mono<String>, который представляет URL для перенаправления
                .flatMap(product -> // Вызов flatMap для выполнения асинхронной операции
                        this.favoriteProductsClient.removeProductFromFavorites(product.id()) // Удаление продукта из избранного
                                .thenReturn("redirect:/customer/products/%d".formatted(product.id()))); // Возврат строки с перенаправлением на страницу продукта после успешного удаления из избранного
    }

    @ExceptionHandler(NoSuchElementException.class)
    public String handleNoSuchElementException(
            NoSuchElementException e,
            Model model
    ) {
        model.addAttribute("error", e.getMessage());
        return "errors/404";
    }
}
