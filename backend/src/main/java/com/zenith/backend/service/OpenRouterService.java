package com.zenith.backend.service;

import com.zenith.backend.dto.ChatCompletionRequest;
import com.zenith.backend.dto.ChatCompletionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.util.List;

@Service
public class OpenRouterService {

    private final RestClient restClient;
    private final String model = "meta-llama/llama-3.1-8b-instruct:free";

    @Autowired
    public OpenRouterService(RestClient restClient) {
        this.restClient = restClient;
    }

    public String ask(String systemPrompt, String userPrompt) {
        String formattedSystem = "You are Zenith AI, an AI Placement Assistant.\n\n" + systemPrompt + 
            "\n\nFormatting Rules:\n- Return clean GitHub Markdown.\n- Use ## for headings.\n- Use bullet points where appropriate.\n- Keep spacing compact.\n- Do NOT add unnecessary blank lines.\n- Use tables only when they improve readability.\n- Be concise but complete.";

        ChatCompletionRequest request = new ChatCompletionRequest(
            model,
            List.of(
                new ChatCompletionRequest.Message("system", formattedSystem),
                new ChatCompletionRequest.Message("user", userPrompt)
            )
        );

        return executeRequest(request);
    }

    public String ask(List<ChatCompletionRequest.Message> messages) {
        ChatCompletionRequest request = new ChatCompletionRequest(model, messages);
        return executeRequest(request);
    }

    private String executeRequest(ChatCompletionRequest request) {
        try {
            ChatCompletionResponse response = restClient.post()
                    .uri("/chat/completions")
                    .body(request)
                    .retrieve()
                    .body(ChatCompletionResponse.class);

            if (response != null && response.choices() != null && !response.choices().isEmpty()) {
                return response.choices().getFirst().message().content();
            }
            return "No response from AI engine.";
        } catch (Exception e) {
            System.err.println("OpenRouter request failed: " + e.getMessage());
            return "Error: Failed to fetch response from OpenRouter. " + e.getMessage();
        }
    }
}
