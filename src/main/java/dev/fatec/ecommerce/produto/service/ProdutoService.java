package dev.fatec.ecommerce.produto.service;

import dev.fatec.ecommerce.produto.model.Produto;
import dev.fatec.ecommerce.produto.repository.ProdutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Transactional(readOnly = true)
    public List<Produto> listarTodos() {
        return produtoRepository.findByAtivoTrue();
    }

    public Optional<Produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Produto> buscarComFiltros(String nome, String marca, Long categoriaId) {
        return produtoRepository.buscarComFiltros(nome, marca, categoriaId);
    }

    @Transactional
    public Produto salvar(Produto produto) {
        produto.calcularValorVenda();
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto atualizar(Long id, Produto produtoAtualizado) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        produto.setNome(produtoAtualizado.getNome());
        produto.setMarca(produtoAtualizado.getMarca());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setIngredientesAtivos(produtoAtualizado.getIngredientesAtivos());
        produto.setDosagemConcentracao(produtoAtualizado.getDosagemConcentracao());
        produto.setFormaFarmaceutica(produtoAtualizado.getFormaFarmaceutica());
        produto.setCategoria(produtoAtualizado.getCategoria());
        produto.setRegistroSanitario(produtoAtualizado.getRegistroSanitario());
        produto.setFabricante(produtoAtualizado.getFabricante());
        produto.setLote(produtoAtualizado.getLote());
        produto.setDataFabricacao(produtoAtualizado.getDataFabricacao());
        produto.setDataValidade(produtoAtualizado.getDataValidade());
        produto.setGrupoPrecificacao(produtoAtualizado.getGrupoPrecificacao());
        produto.setCodigoBarras(produtoAtualizado.getCodigoBarras());
        produto.setPeso(produtoAtualizado.getPeso());
        produto.setAltura(produtoAtualizado.getAltura());
        produto.setLargura(produtoAtualizado.getLargura());
        produto.setProfundidade(produtoAtualizado.getProfundidade());
        produto.setImagemUrl(produtoAtualizado.getImagemUrl());
        produto.setPrescricaoObrigatoria(produtoAtualizado.getPrescricaoObrigatoria());
        produto.setControlado(produtoAtualizado.getControlado());
        produto.setValorCusto(produtoAtualizado.getValorCusto());
        produto.calcularValorVenda();

        return produtoRepository.save(produto);
    }

    @Transactional
    public void inativar(Long id, String motivo) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    @Transactional
    public void ativar(Long id, String motivo) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        produto.setAtivo(true);
        produtoRepository.save(produto);
    }

    @Transactional
    public void atualizarEstoque(Long id, Integer quantidade) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        produto.setEstoque(produto.getEstoque() + quantidade);
        produtoRepository.save(produto);
    }

    @Transactional
    public void inativarProdutosSemEstoque(int diasSemVenda) {
        List<Produto> produtosSemVenda = produtoRepository.findProdutosSemVendaRecente(diasSemVenda);
        produtosSemVenda.forEach(p -> p.setAtivo(false));
        produtoRepository.saveAll(produtosSemVenda);
    }
}
