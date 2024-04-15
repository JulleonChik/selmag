package org.julleon.feedback.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.julleon.feedback.controller.payload.FavoriteProductPayload;
import org.julleon.feedback.entity.FavoriteProduct;
import org.julleon.feedback.service.FavoriteProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/favorite-products")
@RequiredArgsConstructor
public class FavoriteProductsRestController {

    private final FavoriteProductService favoriteProductService;

    //Добавление товара в избранное.
    @PostMapping
    public Mono<ResponseEntity<FavoriteProduct>> addProductToFavorites(
            @Valid @RequestBody Mono<FavoriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return payloadMono
                .flatMap(payload -> this.favoriteProductService.addProductToFavorites(payload.productId()))
                .map(favoriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("feedback-api/favorite-products/{favoriteProductId}").build(favoriteProduct.getId()))
                        .body(favoriteProduct));
    }

    //Удаление товара из избранного.
    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavorites(
            @PathVariable("productId") int productId
    ) {
        return this.favoriteProductService.removeProductFromFavorites(productId)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    //Получение избранного товара по его идентификатору товара.
    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavoriteProduct> getFavoriteProductByProductId(
            @PathVariable("productId") int productId
    ) {
        return this.favoriteProductService.findFavoriteProductByProductId(productId);
    }

    //Получение списка всех избранных товаров.
    @GetMapping
    public Flux<FavoriteProduct> getAllFavoriteProducts() {
        return this.favoriteProductService.findAllFavoriteProducts();
    }
}
