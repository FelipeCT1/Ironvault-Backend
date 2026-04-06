package dev.fatec.ecommerce.venda.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemCompraDTO {

    private Long produtoId;

    private String produtoNome;

    private Integer quantidade;

    private BigDecimal precoUnitario;
}
