package dev.fatec.ecommerce.venda.dto;

import lombok.Data;

@Data
public class EnderecoEntregaDTO {

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
