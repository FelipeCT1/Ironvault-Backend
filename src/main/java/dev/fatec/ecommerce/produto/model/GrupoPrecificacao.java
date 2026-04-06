package dev.fatec.ecommerce.produto.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class GrupoPrecificacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal margemLucro; // Percentual de margem de lucro

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal margemMinima; // Margem mínima permitida

    private Boolean ativo = true;
}
