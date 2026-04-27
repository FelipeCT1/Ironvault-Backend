package dev.fatec.ecommerce.cliente.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;

@Entity
@Data
public class CartaoCredito {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String numero;

    private String nomeImpresso;

    private String bandeira;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String codigoSeguranca;

    private String ultimosDigitos;

    private boolean preferencial;

    @PrePersist
    @PreUpdate
    protected void prePersist() {
        if (numero != null) {
            String digits = numero.replaceAll("\\D", "");
            ultimosDigitos = digits.length() >= 4 ? digits.substring(digits.length() - 4) : digits;
        }
    }
}