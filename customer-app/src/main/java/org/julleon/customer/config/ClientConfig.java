package org.julleon.customer.config;

import org.julleon.customer.client.ProductsWebClient;
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
}
