package dev.fatec.ecommerce.cliente.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "Gênero é obrigatório")
    private String genero;

    @NotNull(message = "Data de nascimento é obrigatória")
    private LocalDate dataNascimento;

    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @Email(message = "E-mail inválido")
    @NotBlank(message = "E-mail é obrigatório")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String senha;

    private Integer ranking = 0;
    private Boolean ativo = true;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Papel papel = Papel.CLIENTE;

    @NotBlank private String tipoTelefone;
    @NotBlank private String ddd;
    @NotBlank private String numeroTelefone;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Endereco> enderecos = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartaoCredito> cartoes = new ArrayList<>();

}