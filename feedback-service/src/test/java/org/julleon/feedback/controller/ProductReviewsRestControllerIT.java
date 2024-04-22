package org.julleon.feedback.controller;


import org.julleon.feedback.controller.payload.ProductReviewPayload;
import org.julleon.feedback.entity.ProductReview;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.headers.HeaderDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureWebTestClient
@ExtendWith(RestDocumentationExtension.class)
class ProductReviewsRestControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReactiveMongoTemplate reactiveMongoTemplate;


    @BeforeEach
    void setUp() {
        ProductReview productReview = this.reactiveMongoTemplate.insertAll(
                List.of(
                        new ProductReview(UUID.fromString("91a9ef9a-8c1e-41f8-81aa-63b88c904188"), "userId_1", 1, 1, "Отзыв №1"),
                        new ProductReview(UUID.fromString("7fde2555-4545-43fe-b0b9-c8c5de6d30ec"), "userId_1", 1, 5, "Отзыв №2"),
                        new ProductReview(UUID.fromString("bc8daf70-f4fd-4f26-8aac-b322b5d32315"), "userId_1", 1, 3, "Отзыв №3")
                )
        ).blockLast();
    }

    @AfterEach
    void tearDown() {
        this.reactiveMongoTemplate.remove(ProductReview.class).all().block();
    }


    @Test
    void testFindProductReviewsByProductId_conditionUserAuthenticated_ReturnsFluxProductReviews() {
//                when
        this.webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt())
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
//                then
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectStatus().isOk()
                .expectBody().json("""
                        [
                            {"id": "91a9ef9a-8c1e-41f8-81aa-63b88c904188", "userId": "userId_1", "productId": 1, "rating": 1, "review": "Отзыв №1"},
                            {"id": "7fde2555-4545-43fe-b0b9-c8c5de6d30ec", "userId": "userId_1", "productId": 1, "rating": 5, "review": "Отзыв №2"},
                            {"id": "bc8daf70-f4fd-4f26-8aac-b322b5d32315", "userId": "userId_1", "productId": 1, "rating": 3, "review": "Отзыв №3"}
                        ]
                                                """);

    }


    @Test
    void testFindProductReviewsByProductId_conditionUserIsNotAuthenticated_ReturnsNotAuthorized() {
//                when
        this.webTestClient
                .get()
                .uri("/feedback-api/product-reviews/by-product-id/1")
                .exchange()
//                then
                .expectStatus().isUnauthorized();
    }

    @Test
    void methodCreateProductReview_conditionRequestIsValid_behaviorReturnsCreatedProductReview() {
        // given Prepare a ProductReviewPayload object
        ProductReviewPayload payload = new ProductReviewPayload(1, 5, "Great product!");
        String subject = "user-tester";

        // when (Perform POST request)
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(builder -> builder.subject(subject).build()))
                .post().uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                            {
                                "productId": 1,
                                "rating": 5,
                                "review": "Great product!"
                            }
                        """)
                .exchange()
                // then
                .expectStatus().isCreated()
                .expectHeader().exists(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").exists()
                .jsonPath("$.userId").isEqualTo(subject)
                .jsonPath("$.productId").isEqualTo(payload.productId())
                .jsonPath("$.rating").isEqualTo(payload.rating())
                .jsonPath("$.review").isEqualTo(payload.review())
                .consumeWith(WebTestClientRestDocumentation
                        .document(
                                "/feedback/product_reviews/create_product_review",

                                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),

                                PayloadDocumentation.requestFields(
                                        PayloadDocumentation.fieldWithPath("productId").type("string").description("Product ID"),
                                        PayloadDocumentation.fieldWithPath("rating").type("int").description("Rating"),
                                        PayloadDocumentation.fieldWithPath("review").type("string").description("Product Review")
                                ),

                                HeaderDocumentation.responseHeaders(
                                        HeaderDocumentation.headerWithName(HttpHeaders.LOCATION).description("Link to the created Product Review")
                                ),
                                PayloadDocumentation.responseFields(
                                        PayloadDocumentation.fieldWithPath("id").type("uuid").description("ProductReview ID"),
                                        PayloadDocumentation.fieldWithPath("userId").type("string").description("User ID"),
                                        PayloadDocumentation.fieldWithPath("productId").type("string").description("Product ID"),
                                        PayloadDocumentation.fieldWithPath("rating").type("int").description("Rating"),
                                        PayloadDocumentation.fieldWithPath("review").type("string").description("Product Review")
                                )
                        )
                );


    }

    @Test
    void methodCreateProductReview_conditionRequestIsInvalid_behaviorReturnsBadRequest() {
        // given
        String subject = "user-tester";

        // when (Perform POST request)
        webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockJwt().jwt(builder -> builder.subject(subject).build()))
                .post().uri("/feedback-api/product-reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                            {
                                "productId": null,
                                "rating": -100,
                                "review": "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed faucibus justo ac ligula porttitor, vitae aliquam quam ultrices. Vivamus vehicula, dui nec luctus gravida, velit leo viverra eros, nec laoreet justo odio vel nunc. Phasellus eget malesuada nunc. Vestibulum et velit ultricies, convallis tellus eget, vestibulum nisi. Ut id tortor nec felis congue rutrum. Proin eget aliquam ipsum. Aliquam erat volutpat. Curabitur commodo mauris et dolor malesuada, vitae ultrices nulla vehicula. Fusce vel condimentum sem. Vestibulum varius tellus nec nulla tincidunt aliquet. Cras dignissim sollicitudin nulla, sed efficitur risus bibendum vel. Integer eu velit eget ex laoreet vehicula. Donec vestibulum finibus ligula, vel scelerisque ipsum fermentum at Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed faucibus justo ac ligula porttitor, vitae aliquam quam ultrices. Vivamus vehicula, dui nec luctus gravida, velit leo viverra eros, nec laoreet justo odio vel nunc. Phasellus eget malesuada nunc. Vestibulum et velit ultricies, convallis tellus eget, vestibulum nisi. Ut id tortor nec felis congue rutrum. Proin eget aliquam ipsum. Aliquam erat volutpat. Curabitur commodo mauris et dolor malesuada, vitae ultrices nulla vehicula. Fusce vel condimentum sem. Vestibulum varius tellus nec nulla tincidunt aliquet. Cras dignissim sollicitudin nulla, sed efficitur risus bibendum vel. Integer eu velit eget ex laoreet vehicula. Donec vestibulum finibus ligula, vel scelerisque ipsum fermentum at."
                            }
                        """)
                .exchange()
                // then
                .expectStatus().isBadRequest()
                .expectHeader().doesNotExist(HttpHeaders.LOCATION)
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON)
                .expectBody()
                .json("""
                        {
                            "errors": [
                                "Идентификатор товара не должен быть null",
                                "Минимальная оценка: 1 звёзд",
                                "Максимальная длина отзыва: 1000 символов"
                            ]
                        }
                        """);
    }
}
