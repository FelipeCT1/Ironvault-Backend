package dev.fatec.ecommerce.venda.service;

import dev.fatec.ecommerce.produto.model.Categoria;
import dev.fatec.ecommerce.produto.model.Produto;
import dev.fatec.ecommerce.produto.repository.CategoriaRepository;
import dev.fatec.ecommerce.produto.repository.ProdutoRepository;
import dev.fatec.ecommerce.venda.dto.DadosMensaisDTO;
import dev.fatec.ecommerce.venda.dto.VendasPorCategoriaDTO;
import dev.fatec.ecommerce.venda.model.ItemVenda;
import dev.fatec.ecommerce.venda.model.Venda;
import dev.fatec.ecommerce.venda.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<VendasPorCategoriaDTO> vendasPorPeriodo(List<Long> categoriaIds, LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.plusDays(1).atStartOfDay();

        List<Venda> vendas = vendaRepository.findByDataCriacaoBetweenFetched(inicio, fim);

        Map<Long, Long> produtoCategoriaMap = produtoRepository.findAll().stream()
                .filter(p -> p.getCategoria() != null)
                .collect(Collectors.toMap(Produto::getId, p -> p.getCategoria().getId()));

        Map<Long, String> categoriaNomeMap = categoriaRepository.findAll().stream()
                .collect(Collectors.toMap(Categoria::getId, Categoria::getNome));

        Set<Long> categoriasFiltro = new HashSet<>(categoriaIds);

        Map<Long, Map<String, Integer>> agregado = new HashMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (Venda venda : vendas) {
            String anoMes = venda.getDataCriacao().format(formatter);

            for (ItemVenda item : venda.getItens()) {
                Long catId = produtoCategoriaMap.get(item.getProdutoId());
                if (catId == null) continue;
                if (!categoriasFiltro.contains(catId)) continue;

                agregado.putIfAbsent(catId, new TreeMap<>());
                Map<String, Integer> meses = agregado.get(catId);
                meses.merge(anoMes, item.getQuantidade(), Integer::sum);
            }
        }

        List<VendasPorCategoriaDTO> resultado = new ArrayList<>();
        for (Long catId : categoriaIds) {
            Map<String, Integer> meses = agregado.getOrDefault(catId, new TreeMap<>());
            String nome = categoriaNomeMap.getOrDefault(catId, "Desconhecida");
            List<DadosMensaisDTO> dados = meses.entrySet().stream()
                    .map(e -> new DadosMensaisDTO(e.getKey(), e.getValue()))
                    .collect(Collectors.toList());
            resultado.add(new VendasPorCategoriaDTO(catId, nome, dados));
        }

        return resultado;
    }
}
