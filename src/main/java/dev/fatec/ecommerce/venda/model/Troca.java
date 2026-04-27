package dev.fatec.ecommerce.venda.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "troca")
@Getter
@Setter
public class Troca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigoTroca;

    @Column(nullable = false)
    private Long vendaId;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private Long produtoId;

    private String produtoNome;

    @Column(nullable = false)
    private Integer quantidade;

    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTroca status;

    private BigDecimal valorCredito;

    private Long cupomGeradoId;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorFrete;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (codigoTroca == null) {
            codigoTroca = "TRC-" + String.format("%04d", System.nanoTime() % 10000);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
