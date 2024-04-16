package org.julleon.customer.config;

import org.julleon.customer.client.impl.FavoriteProductsWebClient;
import org.julleon.customer.client.impl.ProductReviewsWebClient;
import org.julleon.customer.client.impl.ProductsWebClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ClientConfig {

    @Bean
    @Scope("prototype")
    public WebClient.Builder selmagServicesWebClientBuilder(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ServerOAuth2AuthorizedClientRepository authorizedClientRepository
    ) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction filter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrationRepository,
                        authorizedClientRepository);
        filter.setDefaultClientRegistrationId("keycloak");
        return WebClient.builder()
                .filter(filter);
    }

    @Bean
    public ProductsWebClient productsWebClient(
            WebClient.Builder selmagServicesWebClientBuilder,
            @Value("${selmag.services.catalogue.uri:http://localhost:8088}") String serviceUrl
    ) {
        WebClient webClient =
                selmagServicesWebClientBuilder
                        .baseUrl(serviceUrl)
                        .build();
        return new ProductsWebClient(webClient);
    }

    @Bean
    public ProductReviewsWebClient productReviewsWebClient(
            WebClient.Builder selmagServicesWebClientBuilder,
            @Value("${selmag.services.feedback.uri:http://localhost:8889}") String serviceUrl
    ) {
        WebClient webClient =
                selmagServicesWebClientBuilder
                        .baseUrl(serviceUrl)
                        .build();
        return new ProductReviewsWebClient(webClient);
    }


    @Bean
    public FavoriteProductsWebClient favoriteProductsWebClient(
            WebClient.Builder selmagServicesWebClientBuilder,
            @Value("${selmag.services.feedback.uri:http://localhost:8889}") String serviceUrl
    ) {
        WebClient webClient =
                selmagServicesWebClientBuilder
                        .baseUrl(serviceUrl)
                        .build();
        return new FavoriteProductsWebClient(webClient);
    }
}
