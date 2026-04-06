package dev.fatec.ecommerce.venda.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Data
public class PagamentoCartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id")
    private Venda venda;

    private Long cartaoId;

    private String bandeira;

    private String ultimosDigitos;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
}
