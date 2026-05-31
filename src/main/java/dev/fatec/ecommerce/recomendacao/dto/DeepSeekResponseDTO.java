package dev.fatec.ecommerce.recomendacao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeepSeekResponseDTO(
    List<Choice> choices,
    Usage usage
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Choice(
        int index,
        Message message
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Message(
        String role,
        String content
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
        int prompt_tokens,
        int completion_tokens,
        int total_tokens
    ) {}
}
