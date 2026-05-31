package dev.fatec.ecommerce.recomendacao.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.fatec.ecommerce.recomendacao.dto.DeepSeekMessageDTO;
import dev.fatec.ecommerce.recomendacao.dto.DeepSeekRequestDTO;
import dev.fatec.ecommerce.recomendacao.dto.DeepSeekResponseDTO;
import dev.fatec.ecommerce.recomendacao.dto.DeepSeekRespostaParserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class DeepSeekService {

    private final String apiKey;
    private final String apiUrl;
    private final String model;
    private final double temperature;
    private final int maxTokens;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public DeepSeekService(
            @Value("${deepseek.api.key}") String apiKey,
            @Value("${deepseek.api.url}") String apiUrl,
            @Value("${deepseek.api.model}") String model,
            @Value("${deepseek.api.temperature}") double temperature,
            @Value("${deepseek.api.max-tokens}") int maxTokens
    ) {
        this.apiKey = apiKey;
        this.apiUrl = apiUrl;
        this.model = model;
        this.temperature = temperature;
        this.maxTokens = maxTokens;
        this.objectMapper = new ObjectMapper();
        this.restClient = RestClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public DeepSeekRespostaParserDTO perguntar(String systemPrompt, String userMessage) {
        var request = new DeepSeekRequestDTO(
                model,
                List.of(
                        new DeepSeekMessageDTO("system", systemPrompt),
                        new DeepSeekMessageDTO("user", userMessage)
                ),
                temperature,
                maxTokens,
                false
        );

        var response = restClient.post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(DeepSeekResponseDTO.class);

        if (response == null || response.choices() == null || response.choices().isEmpty()) {
            return new DeepSeekRespostaParserDTO(List.of(), "Desculpe, não consegui processar sua solicitação no momento.");
        }

        String content = response.choices().getFirst().message().content();

        try {
            int jsonStart = content.indexOf('{');
            int jsonEnd = content.lastIndexOf('}');
            if (jsonStart != -1 && jsonEnd > jsonStart) {
                String json = content.substring(jsonStart, jsonEnd + 1);
                return objectMapper.readValue(json, DeepSeekRespostaParserDTO.class);
            }
        } catch (Exception e) {
            // Fallback: retorna o texto puro da resposta sem produtos
        }

        return new DeepSeekRespostaParserDTO(List.of(), content);
    }
}
