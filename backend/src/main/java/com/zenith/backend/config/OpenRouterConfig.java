package com.zenith.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class OpenRouterConfig {

    @Bean
    public RestClient openRouterRestClient() {
        String apiKey = System.getProperty("OPENROUTER_API_KEY");
        if (apiKey == null || apiKey.isEmpty()) {
            apiKey = System.getenv("OPENROUTER_API_KEY");
        }
        if (apiKey == null) {
            apiKey = "";
        }

        return RestClient.builder()
                .baseUrl("https://openrouter.ai/api/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey.trim())
                .defaultHeader("Content-Type", "application/json")
                .defaultHeader("HTTP-Referer", "https://zenithai.com")
                .defaultHeader("X-Title", "Zenith AI Placement Intelligence")
                .build();
    }
}
