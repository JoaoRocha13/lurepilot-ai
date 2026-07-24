package com.lurepilot.backend.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;

@Component
public class LmStudioClient {

    private final RestClient restClient;
    private final String model;
    private final Integer maxTokens;

    public LmStudioClient(
            @Value("${lurepilot.ai.base-url}") String baseUrl,
            @Value("${lurepilot.ai.model}") String model,
            @Value("${lurepilot.ai.connect-timeout-seconds}") long connectTimeoutSeconds,
            @Value("${lurepilot.ai.read-timeout-seconds}") long readTimeoutSeconds,
            @Value("${lurepilot.ai.max-tokens}") Integer maxTokens
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(connectTimeoutSeconds));
        requestFactory.setReadTimeout(Duration.ofSeconds(readTimeoutSeconds));

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
        this.model = model;
        this.maxTokens = maxTokens;
    }

    public String createChatCompletion(String systemMessage, String userMessage) {
        ChatCompletionRequest request = new ChatCompletionRequest(
                model,
                List.of(
                        new ChatMessage("system", systemMessage),
                        new ChatMessage("user", userMessage)
                ),
                0.4,
                maxTokens
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
            Double temperature,
            Integer max_tokens
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
