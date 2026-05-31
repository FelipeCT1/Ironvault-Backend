package dev.fatec.ecommerce.recomendacao.dto;

import java.util.List;

public record DeepSeekRequestDTO(
    String model,
    List<DeepSeekMessageDTO> messages,
    double temperature,
    int max_tokens,
    boolean stream
) {}
