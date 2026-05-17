package dev.fatec.ecommerce.venda.service;

import dev.fatec.ecommerce.cupom.model.Cupom;
import dev.fatec.ecommerce.cupom.model.TipoCupom;
import dev.fatec.ecommerce.cupom.repository.CupomRepository;
import dev.fatec.ecommerce.produto.model.Produto;
import dev.fatec.ecommerce.produto.repository.ProdutoRepository;
import dev.fatec.ecommerce.venda.dto.EnderecoEntregaDTO;
import dev.fatec.ecommerce.venda.dto.FinalizarCompraDTO;
import dev.fatec.ecommerce.venda.dto.FreteDTO;
import dev.fatec.ecommerce.venda.dto.ItemCompraDTO;
import dev.fatec.ecommerce.venda.dto.PagamentoCartaoDTO;
import dev.fatec.ecommerce.venda.model.*;
import dev.fatec.ecommerce.venda.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VendaServiceTest {

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private CupomRepository cupomRepository;

    @InjectMocks
    private VendaService vendaService;

    private Produto produto;
    private Cupom cupomPromocional;
    private Cupom cupomTroca;
    private FinalizarCompraDTO dtoBasico;

    @BeforeEach
    void setUp() {
        produto = new Produto();
        produto.setId(1L);
        produto.setNome("Whey Protein");
        produto.setEstoque(10);
        produto.setValorVenda(new BigDecimal("100.00"));

        cupomPromocional = new Cupom();
        cupomPromocional.setId(1L);
        cupomPromocional.setCodigo("PRIMEIRA10");
        cupomPromocional.setTipo(TipoCupom.PROMOCIONAL);
        cupomPromocional.setValor(new BigDecimal("10.00"));
        cupomPromocional.setUtilizado(false);
        cupomPromocional.setAtivo(true);

        cupomTroca = new Cupom();
        cupomTroca.setId(2L);
        cupomTroca.setCodigo("TROCA25");
        cupomTroca.setTipo(TipoCupom.TROCA);
        cupomTroca.setValor(new BigDecimal("25.00"));
        cupomTroca.setUtilizado(false);
        cupomTroca.setAtivo(true);

        EnderecoEntregaDTO endereco = new EnderecoEntregaDTO();
        endereco.setApelido("Casa");
        endereco.setTipoLogradouro("RUA");
        endereco.setLogradouro("Rua A");
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
        item.setProdutoId(1L);
        item.setProdutoNome("Whey Protein");
        item.setQuantidade(2);
        item.setPrecoUnitario(new BigDecimal("100.00"));

        dtoBasico = new FinalizarCompraDTO();
        dtoBasico.setClienteId(1L);
        dtoBasico.setEnderecoEntrega(endereco);
        dtoBasico.setFrete(frete);
        dtoBasico.setItens(List.of(item));
    }

    /* =================================================================
     * CT-01: Cliente realiza compra básica com sucesso
     * ================================================================= */
    @Test
    @DisplayName("CT-01: Cliente realizar compra básica com sucesso")
    void finalizarCompra_DeveCriarVendaComSucesso() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.getNextPedidoNumber()).thenReturn(1L);
        when(vendaRepository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        Venda resultado = vendaService.finalizarCompra(dtoBasico);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getClienteId());
        assertEquals("PED-0001", resultado.getCodigoPedido());
        assertEquals(1, resultado.getItens().size());
        assertEquals(StatusVenda.EM_PROCESSAMENTO, resultado.getStatus());
        assertEquals(0, new BigDecimal("200").compareTo(resultado.getSubtotal()));
        assertEquals(0, new BigDecimal("215").compareTo(resultado.getTotal()));
        assertNotNull(resultado.getEnderecoEntrega());
        assertNotNull(resultado.getFreteTipo());

        verify(vendaRepository).save(any(Venda.class));
    }

    /* =================================================================
     * CT-02: Compra com cupom promocional
     * ================================================================= */
    @Test
    @DisplayName("CT-02: Cliente realizar compra com cupom promocional")
    void finalizarCompra_ComCupomPromocional_DeveAplicarDesconto() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.getNextPedidoNumber()).thenReturn(1L);
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomPromocional));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        dtoBasico.setCupomPromocionalId(1L);
        Venda resultado = vendaService.finalizarCompra(dtoBasico);

        assertEquals(1L, resultado.getCupomPromocionalId());
        assertEquals("PRIMEIRA10", resultado.getCupomPromocionalCodigo());
        assertEquals(0, new BigDecimal("10").compareTo(resultado.getDescontoPromocional()));
        assertEquals(0, new BigDecimal("205").compareTo(resultado.getTotal()));
        assertTrue(cupomPromocional.getUtilizado());
        assertNotNull(cupomPromocional.getDataUtilizacao());
    }

    /* =================================================================
     * CT-03: Compra com cupons de troca
     * ================================================================= */
    @Test
    @DisplayName("CT-03: Cliente realizar compra com cupons de troca")
    void finalizarCompra_ComCuponsTroca_DeveAplicarDescontos() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.getNextPedidoNumber()).thenReturn(1L);
        when(cupomRepository.findById(2L)).thenReturn(Optional.of(cupomTroca));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        dtoBasico.setCuponsTrocaIds(List.of(2L));
        Venda resultado = vendaService.finalizarCompra(dtoBasico);

        assertEquals(1, resultado.getCuponsTrocaIds().size());
        assertTrue(resultado.getCuponsTrocaIds().contains(2L));
        assertEquals(0, new BigDecimal("25").compareTo(resultado.getDescontoTroca()));
        assertEquals(0, new BigDecimal("190").compareTo(resultado.getTotal()));
        assertTrue(cupomTroca.getUtilizado());
    }

    /* =================================================================
     * CT-04: Compra com múltiplos cartões
     * ================================================================= */
    @Test
    @DisplayName("CT-04: Cliente pagar com múltiplos cartões de crédito")
    void finalizarCompra_ComMultiplosCartoes_DeveRegistrarPagamentos() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.getNextPedidoNumber()).thenReturn(1L);
        when(vendaRepository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        PagamentoCartaoDTO cartao1 = new PagamentoCartaoDTO();
        cartao1.setCartaoId(1L);
        cartao1.setBandeira("VISA");
        cartao1.setUltimosDigitos("1234");
        cartao1.setValor(new BigDecimal("100.00"));

        PagamentoCartaoDTO cartao2 = new PagamentoCartaoDTO();
        cartao2.setCartaoId(2L);
        cartao2.setBandeira("MASTERCARD");
        cartao2.setUltimosDigitos("5678");
        cartao2.setValor(new BigDecimal("115.00"));

        dtoBasico.setPagamentosCartao(List.of(cartao1, cartao2));
        Venda resultado = vendaService.finalizarCompra(dtoBasico);

        assertEquals(2, resultado.getPagamentosCartao().size());
        assertEquals(0, new BigDecimal("215").compareTo(resultado.getTotal()));
    }

    /* =================================================================
     * CT-05: Combinação mista (cupom promocional + troca + cartão)
     * ================================================================= */
    @Test
    @DisplayName("CT-05: Cliente pagar com combinação mista (cupom promocional + troca + cartão)")
    void finalizarCompra_ComCupomECartoes_DeveProcessarPagamentoMisto() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.getNextPedidoNumber()).thenReturn(1L);
        when(cupomRepository.findById(1L)).thenReturn(Optional.of(cupomPromocional));
        when(cupomRepository.findById(2L)).thenReturn(Optional.of(cupomTroca));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        PagamentoCartaoDTO cartao = new PagamentoCartaoDTO();
        cartao.setCartaoId(1L);
        cartao.setBandeira("VISA");
        cartao.setUltimosDigitos("1234");
        cartao.setValor(new BigDecimal("180.00"));

        dtoBasico.setCupomPromocionalId(1L);
        dtoBasico.setCuponsTrocaIds(List.of(2L));
        dtoBasico.setPagamentosCartao(List.of(cartao));
        Venda resultado = vendaService.finalizarCompra(dtoBasico);

        assertEquals(0, new BigDecimal("10").compareTo(resultado.getDescontoPromocional()));
        assertEquals(0, new BigDecimal("25").compareTo(resultado.getDescontoTroca()));
        assertEquals(1, resultado.getPagamentosCartao().size());
        // total = (2*100) - 10 - 25 + 15 = 180
        assertEquals(0, new BigDecimal("180").compareTo(resultado.getTotal()));
    }

    /* =================================================================
     * CT-06: Sistema dá baixa no estoque
     * ================================================================= */
    @Test
    @DisplayName("CT-06: Sistema dar baixa no estoque ao finalizar compra")
    void finalizarCompra_DeveDarBaixaNoEstoque() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.getNextPedidoNumber()).thenReturn(1L);
        when(vendaRepository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        vendaService.finalizarCompra(dtoBasico);

        assertEquals(8, produto.getEstoque());
        verify(produtoRepository).save(produto);
    }

    /* =================================================================
     * CT-07: Admin confirma pagamento
     * ================================================================= */
    @Test
    @DisplayName("CT-07: Administrador confirmar pagamento da venda")
    void aprovar_DeveAlterarStatusParaAprovada() {
        Venda venda = new Venda();
        venda.setId(1L);
        venda.setStatus(StatusVenda.EM_PROCESSAMENTO);

        when(vendaRepository.findByIdFetched(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        vendaService.aprovar(1L);

        assertEquals(StatusVenda.APROVADA, venda.getStatus());
        verify(vendaRepository).save(venda);
    }

    /* =================================================================
     * CT-08: Admin reprova + restoca
     * ================================================================= */
    @Test
    @DisplayName("CT-08: Administrador reprovar pagamento e sistema restocar estoque")
    void reprovar_DeveAlterarStatusParaReprovadaERestocar() {
        Venda venda = new Venda();
        venda.setId(1L);
        venda.setStatus(StatusVenda.EM_PROCESSAMENTO);

        ItemVenda item = new ItemVenda();
        item.setProdutoId(1L);
        item.setQuantidade(2);
        venda.getItens().add(item);

        when(vendaRepository.findByIdFetched(1L)).thenReturn(Optional.of(venda));
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        vendaService.reprovar(1L);

        assertEquals(StatusVenda.REPROVADA, venda.getStatus());
        assertEquals(12, produto.getEstoque());
        verify(produtoRepository).save(produto);
    }

    /* =================================================================
     * CT-09: Admin define EM_TRANSPORTE
     * ================================================================= */
    @Test
    @DisplayName("CT-09: Administrador definir que o produto está EM_TRANSPORTE")
    void despachar_DeveAlterarStatusParaEmTransito() {
        Venda venda = new Venda();
        venda.setId(1L);
        venda.setStatus(StatusVenda.APROVADA);

        when(vendaRepository.findByIdFetched(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        vendaService.despachar(1L);

        assertEquals(StatusVenda.EM_TRANSITO, venda.getStatus());
    }

    /* =================================================================
     * CT-10: Admin confirma ENTREGUE
     * ================================================================= */
    @Test
    @DisplayName("CT-10: Administrador confirmar que o produto foi ENTREGUE")
    void entregar_DeveAlterarStatusParaEntregue() {
        Venda venda = new Venda();
        venda.setId(1L);
        venda.setStatus(StatusVenda.EM_TRANSITO);

        when(vendaRepository.findByIdFetched(1L)).thenReturn(Optional.of(venda));
        when(vendaRepository.save(any(Venda.class))).thenAnswer(i -> i.getArgument(0));

        vendaService.entregar(1L);

        assertEquals(StatusVenda.ENTREGUE, venda.getStatus());
    }

    /* =================================================================
     * Cenários de exceção / validação
     * ================================================================= */

    @Test
    @DisplayName("CT-11: Reprovar venda que não está em processamento deve lançar exceção")
    void reprovar_ComStatusInvalido_DeveLancarExcecao() {
        Venda venda = new Venda();
        venda.setId(1L);
        venda.setStatus(StatusVenda.APROVADA);

        when(vendaRepository.findByIdFetched(1L)).thenReturn(Optional.of(venda));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> vendaService.reprovar(1L));
        assertTrue(ex.getMessage().contains("não está em processamento"));
    }

    @Test
    @DisplayName("CT-12: Despachar venda não aprovada deve lançar exceção")
    void despachar_ComStatusInvalido_DeveLancarExcecao() {
        Venda venda = new Venda();
        venda.setId(1L);
        venda.setStatus(StatusVenda.EM_PROCESSAMENTO);

        when(vendaRepository.findByIdFetched(1L)).thenReturn(Optional.of(venda));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> vendaService.despachar(1L));
        assertTrue(ex.getMessage().contains("não está aprovada"));
    }

    @Test
    @DisplayName("CT-13: Confirmar entrega de venda não em trânsito deve lançar exceção")
    void entregar_ComStatusInvalido_DeveLancarExcecao() {
        Venda venda = new Venda();
        venda.setId(1L);
        venda.setStatus(StatusVenda.APROVADA);

        when(vendaRepository.findByIdFetched(1L)).thenReturn(Optional.of(venda));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> vendaService.entregar(1L));
        assertTrue(ex.getMessage().contains("não está em trânsito"));
    }

    @Test
    @DisplayName("CT-14: Compra com estoque insuficiente deve lançar exceção")
    void finalizarCompra_ComEstoqueInsuficiente_DeveLancarExcecao() {
        produto.setEstoque(1);
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> vendaService.finalizarCompra(dtoBasico));
        assertTrue(ex.getMessage().contains("Estoque insuficiente"));
    }

    @Test
    @DisplayName("CT-15: Aprovar venda inexistente deve lançar EntityNotFoundException")
    void aprovar_VendaInexistente_DeveLancarExcecao() {
        when(vendaRepository.findByIdFetched(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> vendaService.aprovar(999L));
    }
}
