package dev.fatec.ecommerce.recomendacao.dto;

import dev.fatec.ecommerce.produto.model.Produto;
import java.math.BigDecimal;
import java.util.List;

public record RespostaChatDTO(
    String resposta,
    List<ProdutoSugerido> produtos,
    List<String> sugestoes
) {
    public record ProdutoSugerido(
        Long id,
        String nome,
        String marca,
        String descricao,
        BigDecimal preco,
        String imagemUrl,
        String categoria,
        Integer estoque
    ) {
        public static ProdutoSugerido fromProduto(Produto p) {
            return new ProdutoSugerido(
                p.getId(),
                p.getNome(),
                p.getMarca(),
                p.getDescricao(),
                p.getValorVenda(),
                p.getImagemUrl(),
                p.getCategoria() != null ? p.getCategoria().getNome() : "",
                p.getEstoque()
            );
        }
    }
}
