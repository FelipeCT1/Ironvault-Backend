package dev.fatec.ecommerce.cupom.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class Cupom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Código do cupom é obrigatório")
    @Column(unique = true, nullable = false)
    private String codigo;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tipo do cupom é obrigatório")
    private TipoCupom tipo;

    @NotNull(message = "Valor do cupom é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal valor;

    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate validoAte;

    // Para cupons de troca, referencia o cliente
    private Long clienteId;

    // Referência à venda que gerou o cupom de troca (se aplicável)
    private Long vendaOrigemId;

    private Boolean utilizado = false;

    private LocalDateTime dataUtilizacao;

    private Boolean ativo = true;

    private LocalDateTime dataCriacao;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    public boolean isValido() {
        return ativo && !utilizado && validoAte.isAfter(LocalDate.now());
    }
}
