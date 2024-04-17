package org.julleon.customer.config;

import org.julleon.customer.client.impl.FavoriteProductsWebClient;
import org.julleon.customer.client.impl.ProductReviewsWebClient;
import org.julleon.customer.client.impl.ProductsWebClient;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TestBeans {

    @Bean
    @Primary
    public ProductsWebClient mockProductsWebClient() {
        return new ProductsWebClient(WebClient.builder().baseUrl("http://localhost:54321").build());
    }

    @Bean
    @Primary
    public ProductReviewsWebClient mockProductReviewsWebClient() {
        return new ProductReviewsWebClient(WebClient.builder().baseUrl("http://localhost:54321").build());
    }


    @Bean
    @Primary
    public FavoriteProductsWebClient mockFavoriteProductsWebClient() {
        return new FavoriteProductsWebClient(WebClient.builder().baseUrl("http://localhost:54321").build());
    }

    @Bean
    public ReactiveClientRegistrationRepository clientRegistrationRepository() {
        return Mockito.mock();
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return Mockito.mock();
    }
}
