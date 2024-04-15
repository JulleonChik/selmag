package org.julleon.customer.controller;

import lombok.RequiredArgsConstructor;
import org.julleon.customer.client.ProductsClient;
import org.julleon.customer.entity.FavoriteProduct;
import org.julleon.customer.entity.Product;
import org.julleon.customer.service.FavoriteProductsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("customer/products")
@RequiredArgsConstructor
public class ProductsController {

    private final ProductsClient productsClient;

    private final FavoriteProductsService favoriteProductsService;

    @GetMapping("list")
    public Mono<String> getPageWithProductsList(
            @RequestParam(name = "filter", required = false) String filter,
            Model model
    ) {
        // Добавляем атрибут "filter" в объект Model
        model.addAttribute("filter", filter);
        // Получаем поток элементов Product из внешнего сервиса productsClient с применением фильтра
        Flux<Product> productsFlux = this.productsClient.getAllProductsWithFilter(filter);
        // Преобразуем поток элементов Product в обещание списка продуктов
        Mono<List<Product>> productListMono = productsFlux.collectList();
        // При успешном завершении обещания списка продуктов добавляем атрибут "products" в объект Model и возвращаем имя представления "customer/products/list"
        return productListMono
                .doOnNext(products -> model.addAttribute("products", products))
                .thenReturn("customer/products/list");
    }

    @GetMapping("favorites")
    public Mono<String> getPageWithFavoritesProductsList(
            @RequestParam(name = "filter", required = false) String filter,
            Model model
    ) {
        model.addAttribute("filter", filter);
        return this.favoriteProductsService.findAllFavoriteProducts()
                .map(FavoriteProduct::getProductId)
                .collectList()
                .flatMap(integers ->
                        this.productsClient.getAllProductsWithFilter(filter)
                                .filter(product -> integers.contains(product.id()))
                                .collectList()
                                .doOnNext(products -> model.addAttribute("products", products))
                ).thenReturn("customer/products/favorites");
    }
}
