package com.demo.kontoservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Aufrufe an den benachrichtigung-service mit denselben Demo-Zugangsdaten (Basic Auth) absichern.
    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder,
        @Value("${demo.auth.username}") String username,
        @Value("${demo.auth.password}") String password) {
            return builder.basicAuthentication(username, password).build();
    }
}
