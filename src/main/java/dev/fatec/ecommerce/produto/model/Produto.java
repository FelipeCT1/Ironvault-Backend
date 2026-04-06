package dev.fatec.ecommerce.produto.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome comercial é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Marca é obrigatória")
    private String marca;

    @Column(length = 1000)
    private String descricao;

    @NotBlank(message = "Ingredientes ativos são obrigatórios")
    private String ingredientesAtivos;

    private String dosagemConcentracao;

    @NotBlank(message = "Forma farmacêutica é obrigatória")
    private String formaFarmaceutica; // CAPSULA, LIQUIDO, INJETAVEL, PO

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    private String registroSanitario;

    @NotBlank(message = "Fabricante é obrigatório")
    private String fabricante;

    private String lote;

    @NotNull(message = "Data de fabricação é obrigatória")
    private LocalDate dataFabricacao;

    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate dataValidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_precificacao_id")
    private GrupoPrecificacao grupoPrecificacao;

    @NotBlank(message = "Código de barras/SKU é obrigatório")
    @Column(unique = true)
    private String codigoBarras;

    private BigDecimal peso; // kg

    private BigDecimal altura; // cm
    private BigDecimal largura; // cm
    private BigDecimal profundidade; // cm

    private String imagemUrl;

    private Boolean prescricaoObrigatoria = false;

    private Boolean controlado = false;

    private Boolean ativo = true;

    private Integer estoque = 0;

    @NotNull(message = "Valor de custo é obrigatório")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCusto;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorVenda;

    private LocalDateTime dataCriacao;

    private LocalDateTime dataAtualizacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        calcularValorVenda();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }

    public void calcularValorVenda() {
        if (valorCusto != null && grupoPrecificacao != null) {
            BigDecimal margem = grupoPrecificacao.getMargemLucro()
                .divide(BigDecimal.valueOf(100));
            this.valorVenda = valorCusto.multiply(BigDecimal.ONE.add(margem));
        }
    }
}
