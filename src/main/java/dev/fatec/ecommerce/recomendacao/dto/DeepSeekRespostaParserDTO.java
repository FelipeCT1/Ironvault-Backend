package dev.fatec.ecommerce.recomendacao.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeepSeekRespostaParserDTO(
    List<Long> produtoIds,
    String resposta
) {}
