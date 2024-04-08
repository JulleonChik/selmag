package org.julleon.manager.config;

import org.julleon.manager.client.DefaultProductsRestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    @Bean
    public DefaultProductsRestClient productsRestClient(
            @Value("${selmag.services.catalogue.uri:http://localhost:8088}") String catalogueBaseUri,
            @Value("${selmag.services.catalogue.username:}") String catalogueUsername,
            @Value("${selmag.services.catalogue.password:}") String cataloguePassword

    ) {
        return new DefaultProductsRestClient(
                RestClient.builder()
                        .baseUrl(catalogueBaseUri)
                        .requestInterceptor(
                                new BasicAuthenticationInterceptor(catalogueUsername, cataloguePassword))
                        .build());
    }

}
