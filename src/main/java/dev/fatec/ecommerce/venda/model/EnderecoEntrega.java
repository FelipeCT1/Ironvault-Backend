package dev.fatec.ecommerce.venda.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class EnderecoEntrega {
    private String apelido;
    private String tipoLogradouro;
    private String logradouro;
    private String numero;
    private String bairro;
    private String cep;
    private String cidade;
    private String estado;
    private String pais;
}
