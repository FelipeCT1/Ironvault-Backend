package dev.fatec.ecommerce.venda.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PagamentoCartao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id")
    @JsonIgnore
    @ToString.Exclude
    private Venda venda;

    private Long cartaoId;

    private String bandeira;

    private String ultimosDigitos;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;
}
