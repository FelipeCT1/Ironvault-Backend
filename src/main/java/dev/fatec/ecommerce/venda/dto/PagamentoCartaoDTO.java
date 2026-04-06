package dev.fatec.ecommerce.venda.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PagamentoCartaoDTO {

    private Long cartaoId;

    private String bandeira;

    private String ultimosDigitos;

    private BigDecimal valor;
}
