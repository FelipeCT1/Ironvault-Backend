package dev.fatec.ecommerce.venda.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class FreteDTO {

    private String tipo;

    private Integer prazoDias;

    private BigDecimal valor;
}
