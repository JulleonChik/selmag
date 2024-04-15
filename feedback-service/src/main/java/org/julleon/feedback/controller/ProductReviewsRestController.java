package org.julleon.feedback.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.julleon.feedback.controller.payload.ProductReviewPayload;
import org.julleon.feedback.entity.ProductReview;
import org.julleon.feedback.service.ProductReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("feedback-api/product-reviews")
@RequiredArgsConstructor
public class ProductReviewsRestController {

    private final ProductReviewService productReviewService;

    //    Создание нового отзыва к товару
    @PostMapping
    public Mono<ResponseEntity<ProductReview>> createProductReview(
            @Valid @RequestBody Mono<ProductReviewPayload> payloadMono,
            UriComponentsBuilder uriComponentsBuilder
    ) {
        return payloadMono
                .flatMap(payload ->
                        this.productReviewService.createProductReview(payload.productId(), payload.rating(), payload.review())
                )
                .map(productReview ->
                        ResponseEntity.created(uriComponentsBuilder
                                        .replacePath("feedback-api/product-reviews/{productReviewId}").build(productReview.getId()))
                                .body(productReview)
                );
    }


    //    Получение списка отзывов о товаре
    @GetMapping("by-product-id/{productId:\\d+}")
    public Flux<ProductReview> findProductReviewsByProductId(
            @PathVariable("productId") int productId
    ) {
        return this.productReviewService.findProductReviewsByProductId(productId);
    }

}
