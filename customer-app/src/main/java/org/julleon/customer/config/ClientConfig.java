package org.julleon.customer.config;

import org.julleon.customer.client.FavoriteProductsClient;
import org.julleon.customer.client.impl.FavoriteProductsWebClient;
import org.julleon.customer.client.impl.ProductReviewsWebClient;
import org.julleon.customer.client.impl.ProductsWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    public ProductsWebClient productsWebClient(
            @Value("${selmag.services.catalogue.uri:http://localhost:8088}") String serviceUrl
    ) {
        WebClient webClient = WebClient.builder()
                .baseUrl(serviceUrl)
                .build();
        return new ProductsWebClient(webClient);
    }

    @Bean
    public ProductReviewsWebClient productReviewsWebClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8889}") String serviceUrl
    ) {
        return new ProductReviewsWebClient(WebClient.builder()
                .baseUrl(serviceUrl)
                .build());
    }


    @Bean
    public FavoriteProductsWebClient favoriteProductsWebClient(
            @Value("${selmag.services.feedback.uri:http://localhost:8889}") String serviceUrl
    ) {
        return new FavoriteProductsWebClient(WebClient.builder()
                .baseUrl(serviceUrl)
                .build());
    }
}
