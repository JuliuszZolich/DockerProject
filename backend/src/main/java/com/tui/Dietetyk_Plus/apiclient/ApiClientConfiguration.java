package com.tui.Dietetyk_Plus.apiclient;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;

@Configuration
public class ApiClientConfiguration {
    @Bean
    public Builder webClientBuilder() {
        return WebClient.builder();
    }
}