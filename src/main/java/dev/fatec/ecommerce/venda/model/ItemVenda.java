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
public class ItemVenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venda_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    private Venda venda;

    @Column(nullable = false)
    private Long produtoId;

    private String produtoNome;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    protected void calcularSubtotal() {
        if (precoUnitario != null && quantidade != null) {
            subtotal = precoUnitario.multiply(BigDecimal.valueOf(quantidade));
        }
    }
}
