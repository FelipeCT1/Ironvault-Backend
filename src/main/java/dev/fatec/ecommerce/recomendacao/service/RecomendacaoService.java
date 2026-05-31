package dev.fatec.ecommerce.recomendacao.service;

import dev.fatec.ecommerce.cliente.model.Cliente;
import dev.fatec.ecommerce.cliente.repository.ClienteRepository;
import dev.fatec.ecommerce.produto.model.Produto;
import dev.fatec.ecommerce.produto.repository.ProdutoRepository;
import dev.fatec.ecommerce.recomendacao.dto.DeepSeekRespostaParserDTO;
import dev.fatec.ecommerce.recomendacao.dto.RespostaChatDTO;
import dev.fatec.ecommerce.recomendacao.dto.RespostaChatDTO.ProdutoSugerido;
import dev.fatec.ecommerce.venda.model.ItemVenda;
import dev.fatec.ecommerce.venda.model.Venda;
import dev.fatec.ecommerce.venda.repository.VendaRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecomendacaoService {

    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final ClienteRepository clienteRepository;
    private final DeepSeekService deepSeekService;

    private static final List<String> SUGESTOES_PADRAO = List.of(
        "Qual o melhor suplemento para ganhar massa muscular?",
        "Preciso de um pré-treino estimulante",
        "Quero emagrecer mantendo músculo",
        "Diferença entre Whey Concentrado e Isolado"
    );

    public RespostaChatDTO processarMensagem(String mensagem, HttpSession session) {
        Long clienteId = (Long) session.getAttribute("clienteId");

        List<Produto> produtosAtivos = produtoRepository.findByAtivoTrue();
        String historicoCompras = buildHistoricoCompras(clienteId);

        String catalogoStr = buildCatalogoContext(produtosAtivos);

        String systemPrompt = """
Você é um assistente de e-commerce de suplementos esportivos da loja IronVault.
Você tem acesso ao seguinte catálogo de produtos (formato: id | nome | categoria | descricao | preco):

%s

Histórico de compras do cliente: %s

REGRAS ABSOLUTAS (NUNCA as quebre):
1. Apenas recomende produtos presentes EXATAMENTE na lista acima.
2. NUNCA invente produtos, marcas, preços ou IDs que não estão no catálogo.
3. Se o usuário pedir algo que não está no catálogo, avise educadamente que não tem disponível.
4. Responda SEMPRE no formato JSON: { "produtoIds": [1,5,8], "resposta": "texto aqui" }
5. produtoIds deve conter apenas IDs da lista acima.
6. Seja educado e útil, mas nunca minta sobre produtos.
""".formatted(catalogoStr, historicoCompras);

        DeepSeekRespostaParserDTO respostaIA = deepSeekService.perguntar(systemPrompt, mensagem);

        List<Long> idsValidos = validarProdutos(respostaIA.produtoIds(), produtosAtivos);

        List<ProdutoSugerido> produtosRecomendados = produtosAtivos.stream()
                .filter(p -> idsValidos.contains(p.getId()))
                .map(ProdutoSugerido::fromProduto)
                .toList();

        String respostaTexto = respostaIA.resposta();
        if (produtosRecomendados.isEmpty() && !respostaIA.produtoIds().isEmpty()) {
            respostaTexto = "Desculpe, não encontrei produtos correspondentes no nosso catálogo. Pode tentar perguntar de outra forma?";
        }

        return new RespostaChatDTO(respostaTexto, produtosRecomendados, SUGESTOES_PADRAO);
    }

    private String buildCatalogoContext(List<Produto> produtos) {
        return produtos.stream()
                .filter(Produto::getAtivo)
                .map(p -> {
                    String cat = p.getCategoria() != null ? p.getCategoria().getNome() : "Sem categoria";
                    return "%d | %s | %s | %s | R$%.2f"
                            .formatted(p.getId(), p.getNome(), cat,
                                    p.getDescricao() != null ? p.getDescricao().replace("\n", " ") : "",
                                    p.getValorVenda() != null ? p.getValorVenda() : 0);
                })
                .collect(Collectors.joining("\n"));
    }

    private String buildHistoricoCompras(Long clienteId) {
        if (clienteId == null) return "Cliente não logado (visita anônima)";

        Optional<Cliente> clienteOpt = clienteRepository.findById(clienteId);
        if (clienteOpt.isEmpty()) return "Cliente não encontrado";

        Cliente cliente = clienteOpt.get();
        List<Venda> vendas = vendaRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId);

        if (vendas.isEmpty()) {
            return "Cliente " + cliente.getNome() + " não possui histórico de compras.";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Cliente: ").append(cliente.getNome()).append("\n");
        sb.append("Compras recentes:\n");

        for (Venda venda : vendas.stream().limit(5).toList()) {
            sb.append("- Pedido ").append(venda.getCodigoPedido()).append(": ");
            for (ItemVenda item : venda.getItens()) {
                sb.append(item.getQuantidade()).append("x ")
                  .append(item.getProdutoNome()).append(", ");
            }
            if (sb.charAt(sb.length() - 2) == ',') {
                sb.setLength(sb.length() - 2);
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    private List<Long> validarProdutos(List<Long> idsSugeridos, List<Produto> produtosAtivos) {
        if (idsSugeridos == null || idsSugeridos.isEmpty()) return List.of();

        Set<Long> idsExistentes = produtosAtivos.stream()
                .map(Produto::getId)
                .collect(Collectors.toSet());

        return idsSugeridos.stream()
                .filter(idsExistentes::contains)
                .toList();
    }
}
