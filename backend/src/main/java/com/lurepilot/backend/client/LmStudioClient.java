package com.lurepilot.backend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class LmStudioClient {

    private final RestClient restClient;
    private final String model;

    public LmStudioClient(
            @Value("${lurepilot.ai.base-url}") String baseUrl,
            @Value("${lurepilot.ai.model}") String model
    ) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
        this.model = model;
    }

    public String createChatCompletion(String systemMessage, String userMessage) {
        ChatCompletionRequest request = new ChatCompletionRequest(
                model,
                List.of(
                        new ChatMessage("system", systemMessage),
                        new ChatMessage("user", userMessage)
                ),
                0.4
        );

        ChatCompletionResponse response = restClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(ChatCompletionResponse.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            throw new IllegalStateException("LM Studio returned an empty response");
        }

        ChatMessage message = response.choices().getFirst().message();
        if (message == null || message.content() == null || message.content().isBlank()) {
            throw new IllegalStateException("LM Studio returned an empty message");
        }

        return message.content();
    }

    private record ChatCompletionRequest(
            String model,
            List<ChatMessage> messages,
            Double temperature
    ) {
    }

    private record ChatMessage(
            String role,
            String content
    ) {
    }

    private record ChatCompletionResponse(
            List<ChatChoice> choices
    ) {
    }

    private record ChatChoice(
            ChatMessage message
    ) {
    }
}
