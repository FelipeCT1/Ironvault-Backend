package dev.fatec.ecommerce.venda.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class FinalizarCompraDTO {
    private Long clienteId;
    private List<ItemCompraDTO> itens;

    private EnderecoEntregaDTO enderecoEntrega;

    private FreteDTO frete;

    private List<PagamentoCartaoDTO> pagamentosCartao;

    private Long cupomPromocionalId;
    private List<Long> cuponsTrocaIds;
}

@Data
class ItemCompraDTO {
    private Long produtoId;
    private String produtoNome;
    private Integer quantidade;
    private BigDecimal precoUnitario;
}

@Data
class EnderecoEntregaDTO {
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

@Data
class FreteDTO {
    private String tipo;
    private Integer prazoDias;
    private BigDecimal valor;
}

@Data
class PagamentoCartaoDTO {
    private Long cartaoId;
    private String bandeira;
    private String ultimosDigitos;
    private BigDecimal valor;
}
