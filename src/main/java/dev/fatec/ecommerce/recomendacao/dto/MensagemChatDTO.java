package dev.fatec.ecommerce.recomendacao.dto;

import jakarta.validation.constraints.NotBlank;

public record MensagemChatDTO(
    @NotBlank(message = "Mensagem é obrigatória")
    String mensagem
) {}
