package dev.fatec.ecommerce.venda.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@Table(name = "venda")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(unique = true)
    private String codigoPedido;

    @NotNull
    private Long clienteId;

    private String clienteNome;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ItemVenda> itens = new LinkedHashSet<>();

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PagamentoCartao> pagamentosCartao = new LinkedHashSet<>();

    @Embedded
    private EnderecoEntrega enderecoEntrega;

    private String freteTipo;

    private Integer fretePrazoDias;

    @Column(precision = 10, scale = 2)
    private BigDecimal freteValor;

    private Long cupomPromocionalId;

    private String cupomPromocionalCodigo;

    @ElementCollection
    @CollectionTable(name = "venda_cupons_troca", joinColumns = @JoinColumn(name = "venda_id"))
    @Column(name = "cupom_troca_id")
    private List<Long> cuponsTrocaIds = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(precision = 10, scale = 2)
    private BigDecimal descontoPromocional = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal descontoTroca = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorFrete;

    @Column(precision = 10, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @NotNull
    private StatusVenda status = StatusVenda.EM_PROCESSAMENTO;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now(); 
        if (codigoPedido == null) {
            codigoPedido = "PED-" + String.format("%04d", id != null ? id : System.currentTimeMillis() % 10000);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public void calcularTotais() {
        subtotal = itens.stream()
            .map(ItemVenda::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        valorFrete = freteValor != null ? freteValor : BigDecimal.ZERO;

        total = subtotal
            .subtract(descontoPromocional != null ? descontoPromocional : BigDecimal.ZERO)
            .subtract(descontoTroca != null ? descontoTroca : BigDecimal.ZERO)
            .add(valorFrete);
    }
}
