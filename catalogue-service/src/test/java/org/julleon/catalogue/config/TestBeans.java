package org.julleon.catalogue.config;

import org.mockito.Mockito;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Configuration
public class TestBeans {


    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:16"));
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return Mockito.mock();
    }
}
