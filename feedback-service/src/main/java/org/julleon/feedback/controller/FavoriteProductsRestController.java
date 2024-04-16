package org.julleon.feedback.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.julleon.feedback.controller.payload.FavoriteProductPayload;
import org.julleon.feedback.entity.FavoriteProduct;
import org.julleon.feedback.service.FavoriteProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RestController
@RequestMapping("feedback-api/favorite-products")
@RequiredArgsConstructor
public class FavoriteProductsRestController {

    private final FavoriteProductService favoriteProductService;

    //Добавление товара в избранное.
    @PostMapping
    public Mono<ResponseEntity<FavoriteProduct>> addProductToFavorites(
            Mono<JwtAuthenticationToken> jwtAuthenticationTokenMono,
            @Valid @RequestBody Mono<FavoriteProductPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return Mono.zip(jwtAuthenticationTokenMono, payloadMono)
                .flatMap(tuple2 ->
                        this.favoriteProductService.addProductToFavorites(tuple2.getT1().getToken().getSubject(), tuple2.getT2().productId()))
                .map(favoriteProduct -> ResponseEntity
                        .created(uriComponentsBuilder.replacePath("feedback-api/favorite-products/{favoriteProductId}").build(favoriteProduct.getId()))
                        .body(favoriteProduct));
    }

    //Удаление товара из избранного.
    @DeleteMapping("by-product-id/{productId:\\d+}")
    public Mono<ResponseEntity<Void>> removeProductFromFavorites(
            Mono<JwtAuthenticationToken> jwtAuthenticationTokenMono,
            @PathVariable("productId") int productId
    ) {
        return jwtAuthenticationTokenMono.flatMap(jwtAuthenticationToken ->
                        this.favoriteProductService.removeProductFromFavorites(jwtAuthenticationToken.getToken().getSubject(), productId))
                .then(Mono.just(ResponseEntity.noContent().build()));
    }

    //Получение избранного товара по его идентификатору товара.
    @GetMapping("by-product-id/{productId:\\d+}")
    public Mono<FavoriteProduct> getFavoriteProductByUserAndProductId(
            Mono<JwtAuthenticationToken> jwtAuthenticationTokenMono,
            @PathVariable("productId") int productId
    ) {
        return jwtAuthenticationTokenMono.flatMap(jwtAuthenticationToken ->
                this.favoriteProductService
                        .findFavoriteProductByUserAndProductId(jwtAuthenticationToken.getToken().getSubject(), productId));
    }

    //Получение списка всех избранных товаров.
    @GetMapping
    public Flux<FavoriteProduct> getAllFavoriteProductsByUser(
            Mono<JwtAuthenticationToken> jwtAuthenticationTokenMono
    ) {
        return jwtAuthenticationTokenMono.flatMapMany(jwtAuthenticationToken ->
                this.favoriteProductService.findAllFavoriteProductsByUserId(jwtAuthenticationToken.getToken().getSubject()));
    }
}
