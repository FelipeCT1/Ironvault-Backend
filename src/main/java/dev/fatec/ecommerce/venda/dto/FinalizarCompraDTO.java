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
