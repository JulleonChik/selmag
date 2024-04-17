package org.julleon.customer.controller;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
@WireMockTest(httpPort = 54321)
class ProductControllerIT {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void methodAddProductToFavorites_conditionRequestIsValid_behaviorReturnsRedirectionToProductPage() {
        // Готовим

        // Мокаем поведение ProductsClient
        String bodyToBeReturned = """
                {"id": 1, "title": "Мандаринка", "description": "Оранжевая и круглая"}
                """;
        MappingBuilder productsClientMapping =
                WireMock
                        .get("/catalogue-api/products/1")
                        .willReturn(WireMock.okJson(bodyToBeReturned)
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));
        WireMock.stubFor(productsClientMapping);

        // Мокаем поведение FavoriteProductsClient
        String requestBodyToBeSent = """
                {"productId" : 1}
                """;
        MappingBuilder favoriteProductsClientMapping = WireMock
                .post("/feedback-api/favorite-products")
                .withRequestBody(WireMock.equalToJson(requestBodyToBeSent))
                .withHeader(HttpHeaders.CONTENT_TYPE, WireMock.equalTo(MediaType.APPLICATION_JSON_VALUE))
                .willReturn(
                        WireMock.created()
                                .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                .withBody("""
                                        {
                                            "id": "5d1fafd5-cf60-46fc-ac70-361a0f4c3141",
                                            "productId": 1
                                        }
                                        """)
                );
        WireMock.stubFor(favoriteProductsClientMapping);

        // Когда
        this.webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("/customer/products/1/add-to-favorites")
                .exchange()
                // Тогда
                .expectStatus().is3xxRedirection()
                .expectHeader().location("/customer/products/1");

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/1")));
        WireMock.verify(WireMock
                .postRequestedFor(WireMock.urlPathMatching("/feedback-api/favorite-products"))
                .withRequestBody(WireMock.equalToJson("""
                        {"productId" : 1}
                        """))
        );
    }

    @Test
    void methodAddProductToFavorites_conditionProductDoesNotExist_behaviorReturnsNotFoundPage() {
        // Готовим
        // Когда
        this.webTestClient
                .mutateWith(SecurityMockServerConfigurers.mockUser())
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post().uri("/customer/products/1/add-to-favorites")
                .exchange()
                // Тогда
                .expectStatus().isNotFound();

        WireMock.verify(WireMock.getRequestedFor(WireMock.urlPathMatching("/catalogue-api/products/1")));
    }
}
