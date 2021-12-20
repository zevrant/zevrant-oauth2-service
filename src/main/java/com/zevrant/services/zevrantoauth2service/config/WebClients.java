package com.zevrant.services.zevrantoauth2service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClients {

    @Bean(name = "keycloakClient")
    public WebClient keycloakClient(@Value("${zevrant.services.keycloak.baseUrl}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }


}
