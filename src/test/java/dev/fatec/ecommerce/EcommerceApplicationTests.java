package dev.fatec.ecommerce;

import dev.fatec.ecommerce.cupom.model.Cupom;
import dev.fatec.ecommerce.cupom.model.TipoCupom;
import dev.fatec.ecommerce.cupom.repository.CupomRepository;
import dev.fatec.ecommerce.produto.model.Categoria;
import dev.fatec.ecommerce.produto.model.GrupoPrecificacao;
import dev.fatec.ecommerce.produto.model.Produto;
import dev.fatec.ecommerce.produto.repository.CategoriaRepository;
import dev.fatec.ecommerce.produto.repository.GrupoPrecificacaoRepository;
import dev.fatec.ecommerce.produto.repository.ProdutoRepository;
import dev.fatec.ecommerce.venda.dto.*;
import dev.fatec.ecommerce.venda.model.*;
import dev.fatec.ecommerce.venda.repository.TrocaRepository;
import dev.fatec.ecommerce.venda.repository.VendaRepository;
import dev.fatec.ecommerce.venda.service.TrocaService;
import dev.fatec.ecommerce.venda.service.VendaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EcommerceApplicationTests {

    @Autowired
    private VendaService vendaService;

    @Autowired
    private TrocaService trocaService;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private CupomRepository cupomRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private TrocaRepository trocaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private GrupoPrecificacaoRepository grupoPrecificacaoRepository;

    private Produto produto;
    private Cupom cupomPromocional;
    private Cupom cupomTroca;

    @BeforeEach
    void setUp() {
        // Limpar dados anteriores
        trocaRepository.deleteAll();
        vendaRepository.deleteAll();
        cupomRepository.deleteAll();
        produtoRepository.deleteAll();

        // Criar dados básicos
        Categoria cat = new Categoria();
        cat.setNome("TESTE");
        cat.setDescricao("Categoria de teste");
        cat = categoriaRepository.save(cat);

        GrupoPrecificacao gp = new GrupoPrecificacao();
        gp.setNome("Teste");
        gp.setMargemLucro(new BigDecimal("30.00"));
        gp.setMargemMinima(new BigDecimal("20.00"));
        gp = grupoPrecificacaoRepository.save(gp);

        produto = new Produto();
        produto.setNome("Produto Teste");
        produto.setMarca("Marca Teste");
        produto.setDescricao("Descricao");
        produto.setIngredientesAtivos("Ingredientes");
        produto.setFormaFarmaceutica("CAPSULA");
        produto.setCategoria(cat);
        produto.setGrupoPrecificacao(gp);
        produto.setFabricante("Fabricante");
        produto.setCodigoBarras("789" + System.currentTimeMillis());
        produto.setDataFabricacao(LocalDate.now().minusMonths(3));
        produto.setDataValidade(LocalDate.now().plusYears(2));
        produto.setValorCusto(new BigDecimal("50.00"));
        produto.setEstoque(10);
        produto = produtoRepository.save(produto);

        cupomPromocional = new Cupom();
        cupomPromocional.setCodigo("INTEGRA10");
        cupomPromocional.setTipo(TipoCupom.PROMOCIONAL);
        cupomPromocional.setValor(new BigDecimal("10.00"));
        cupomPromocional.setValidoAte(LocalDate.now().plusMonths(6));
        cupomPromocional = cupomRepository.save(cupomPromocional);

        cupomTroca = new Cupom();
        cupomTroca.setCodigo("INTEGRA25");
        cupomTroca.setTipo(TipoCupom.TROCA);
        cupomTroca.setValor(new BigDecimal("25.00"));
        cupomTroca.setValidoAte(LocalDate.now().plusMonths(6));
        cupomTroca.setClienteId(1L);
        cupomTroca = cupomRepository.save(cupomTroca);
    }

    /* =================================================================
     * IT-01: Fluxo completo de venda + troca
     * ================================================================= */
    @Test
    @DisplayName("IT-01: Fluxo completo - compra > aprovação > transporte > entrega > troca > autorização > conclusão")
    void fluxoCompletoVendaETroca() {
        // ========== ETAPA 1: Cliente realiza a compra ==========
        EnderecoEntregaDTO endereco = new EnderecoEntregaDTO();
        endereco.setApelido("Casa");
        endereco.setTipoLogradouro("RUA");
        endereco.setLogradouro("Rua das Flores");
        endereco.setNumero("123");
        endereco.setBairro("Centro");
        endereco.setCep("12345-678");
        endereco.setCidade("São Paulo");
        endereco.setEstado("SP");
        endereco.setPais("Brasil");

        FreteDTO frete = new FreteDTO();
        frete.setTipo("SEDEX");
        frete.setPrazoDias(3);
        frete.setValor(new BigDecimal("15.00"));

        ItemCompraDTO item = new ItemCompraDTO();
        item.setProdutoId(produto.getId());
        item.setProdutoNome(produto.getNome());
        item.setQuantidade(2);
        item.setPrecoUnitario(produto.getValorVenda());

        PagamentoCartaoDTO pagamento = new PagamentoCartaoDTO();
        pagamento.setCartaoId(1L);
        pagamento.setBandeira("VISA");
        pagamento.setUltimosDigitos("1234");
        pagamento.setValor(produto.getValorVenda().multiply(BigDecimal.valueOf(2))
            .add(new BigDecimal("15.00"))
            .subtract(new BigDecimal("10.00"))
            .subtract(new BigDecimal("25.00")));

        FinalizarCompraDTO compraDTO = new FinalizarCompraDTO();
        compraDTO.setClienteId(1L);
        compraDTO.setEnderecoEntrega(endereco);
        compraDTO.setFrete(frete);
        compraDTO.setItens(List.of(item));
        compraDTO.setPagamentosCartao(List.of(pagamento));
        compraDTO.setCupomPromocionalId(cupomPromocional.getId());
        compraDTO.setCuponsTrocaIds(List.of(cupomTroca.getId()));

        Venda venda = vendaService.finalizarCompra(compraDTO);

        assertNotNull(venda);
        assertNotNull(venda.getId());
        assertEquals(StatusVenda.EM_PROCESSAMENTO, venda.getStatus());
        assertEquals(1, venda.getItens().size());
        assertEquals(1, venda.getPagamentosCartao().size());
        assertTrue(venda.getCodigoPedido().startsWith("PED-"));
        assertEquals(0, new BigDecimal("10").compareTo(venda.getDescontoPromocional()));
        assertEquals(0, new BigDecimal("25").compareTo(venda.getDescontoTroca()));

        // Verificar baixa de estoque
        Produto pAtualizado = produtoRepository.findById(produto.getId()).orElseThrow();
        assertEquals(8, pAtualizado.getEstoque());

        Long vendaId = venda.getId();

        // ========== ETAPA 2: Admin aprova pagamento ==========
        vendaService.aprovar(vendaId);
        venda = vendaRepository.findByIdFetched(vendaId).orElseThrow();
        assertEquals(StatusVenda.APROVADA, venda.getStatus());

        // ========== ETAPA 3: Admin define EM_TRANSPORTE ==========
        vendaService.despachar(vendaId);
        venda = vendaRepository.findByIdFetched(vendaId).orElseThrow();
        assertEquals(StatusVenda.EM_TRANSITO, venda.getStatus());

        // ========== ETAPA 4: Admin confirma ENTREGUE ==========
        vendaService.entregar(vendaId);
        venda = vendaRepository.findByIdFetched(vendaId).orElseThrow();
        assertEquals(StatusVenda.ENTREGUE, venda.getStatus());

        // ========== ETAPA 5: Usuário solicita troca ==========
        SolicitarTrocaDTO trocaDTO = new SolicitarTrocaDTO();
        trocaDTO.setVendaId(vendaId);
        trocaDTO.setProdutoId(produto.getId());
        trocaDTO.setProdutoNome(produto.getNome());
        trocaDTO.setQuantidade(1);
        trocaDTO.setMotivo("Produto veio com defeito");
        trocaDTO.setValorCredito(new BigDecimal("80.00"));

        Troca troca = trocaService.solicitar(trocaDTO);
        assertNotNull(troca);
        assertNotNull(troca.getId());
        assertEquals(StatusTroca.SOLICITADA, troca.getStatus());
        assertEquals(1, troca.getQuantidade());

        Long trocaId = troca.getId();

        // ========== ETAPA 6: Admin autoriza troca ==========
        Troca trocaAutorizada = trocaService.autorizar(trocaId);
        assertEquals(StatusTroca.AUTORIZADA, trocaAutorizada.getStatus());
        assertNotNull(trocaAutorizada.getCupomGeradoId());

        // Verificar que a venda foi atualizada para EM_TROCA
        venda = vendaRepository.findByIdFetched(vendaId).orElseThrow();
        assertEquals(StatusVenda.EM_TROCA, venda.getStatus());

        // Verificar que o cupom de troca foi gerado
        Cupom cupomGerado = cupomRepository.findById(trocaAutorizada.getCupomGeradoId()).orElseThrow();
        assertEquals(TipoCupom.TROCA, cupomGerado.getTipo());
        assertEquals(0, new BigDecimal("80.00").compareTo(cupomGerado.getValor()));
        assertEquals(1L, cupomGerado.getClienteId().longValue());
        assertFalse(cupomGerado.getUtilizado());
        assertTrue(cupomGerado.getAtivo());

        // ========== ETAPA 7: Admin conclui troca (confirma recebimento) ==========
        Troca trocaConcluida = trocaService.concluir(trocaId);
        assertEquals(StatusTroca.CONCLUIDA, trocaConcluida.getStatus());

        // Verificar que a venda foi atualizada para TROCADO
        venda = vendaRepository.findByIdFetched(vendaId).orElseThrow();
        assertEquals(StatusVenda.TROCADO, venda.getStatus());
    }

    /* =================================================================
     * IT-02: Fluxo de reprovação com restoque
     * ================================================================= */
    @Test
    @DisplayName("IT-02: Fluxo de reprovação - admin reprova e estoque é restocado")
    void fluxoReprovacaoRestocaEstoque() {
        int estoqueInicial = produto.getEstoque();

        // Finalizar compra
        ItemCompraDTO item = new ItemCompraDTO();
        item.setProdutoId(produto.getId());
        item.setProdutoNome(produto.getNome());
        item.setQuantidade(3);
        item.setPrecoUnitario(produto.getValorVenda());

        FinalizarCompraDTO dto = new FinalizarCompraDTO();
        dto.setClienteId(1L);
        dto.setItens(List.of(item));
        dto.setEnderecoEntrega(criarEnderecoDTO());
        dto.setFrete(criarFreteDTO());

        Venda venda = vendaService.finalizarCompra(dto);
        assertEquals(estoqueInicial - 3, produtoRepository.findById(produto.getId()).orElseThrow().getEstoque());

        // Reprovar
        vendaService.reprovar(venda.getId());

        Venda vendaReprovada = vendaRepository.findByIdFetched(venda.getId()).orElseThrow();
        assertEquals(StatusVenda.REPROVADA, vendaReprovada.getStatus());

        // Estoque deve ter sido restocado
        assertEquals(estoqueInicial, produtoRepository.findById(produto.getId()).orElseThrow().getEstoque());
    }

    private EnderecoEntregaDTO criarEnderecoDTO() {
        EnderecoEntregaDTO e = new EnderecoEntregaDTO();
        e.setApelido("Casa");
        e.setTipoLogradouro("RUA");
        e.setLogradouro("Rua Teste");
        e.setNumero("1");
        e.setBairro("Centro");
        e.setCep("11111-111");
        e.setCidade("Cidade");
        e.setEstado("SP");
        e.setPais("Brasil");
        return e;
    }

    private FreteDTO criarFreteDTO() {
        FreteDTO f = new FreteDTO();
        f.setTipo("PAC");
        f.setPrazoDias(10);
        f.setValor(new BigDecimal("10.00"));
        return f;
    }
}
