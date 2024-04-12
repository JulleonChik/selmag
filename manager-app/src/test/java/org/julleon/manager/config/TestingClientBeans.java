package org.julleon.manager.config;


import org.julleon.manager.client.DefaultProductsRestClient;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.web.client.RestClient;

@Configuration
public class TestingClientBeans {

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return Mockito.mock(ClientRegistrationRepository.class);
    }

    @Bean
    public OAuth2AuthorizedClientRepository authorizedClientRepository() {
        return Mockito.mock(OAuth2AuthorizedClientRepository.class);
    }

    @Bean
    @Primary
    public DefaultProductsRestClient testProductsRestClient(
            @Value("${selmag.services.catalogue.uri:http://localhost:12341}") String catalogueBaseUri
            ) {
        return new DefaultProductsRestClient(RestClient.builder()
                .baseUrl(catalogueBaseUri)
                .build());
    }
}
