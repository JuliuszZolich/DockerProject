package com.tui.Dietetyk_Plus.apiclient;

import com.tui.Dietetyk_Plus.database.models.Ingredient;
import com.tui.Dietetyk_Plus.database.models.objects.Macros;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient.Builder;

@Service
public class ApiClient {

    private final WebClient webClient;

    @Autowired
    public ApiClient(Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://ai:4000").build();
    }

    public Macros getNutritionalValuesForIngredient(Ingredient ingredient) {
        return webClient.post()
                .uri("/api/ingredient")
                .body(BodyInserters.fromValue(ingredient))
                .retrieve()
                .bodyToMono(Macros.class)
                .block();
    }
}