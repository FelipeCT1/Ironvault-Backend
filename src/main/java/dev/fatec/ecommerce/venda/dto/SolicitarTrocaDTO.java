package dev.fatec.ecommerce.venda.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SolicitarTrocaDTO {
    private Long vendaId;
    private Long produtoId;
    private String produtoNome;
    private Integer quantidade;
    private String motivo;
    private BigDecimal valorCredito;
}
