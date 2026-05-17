package dev.fatec.ecommerce.venda.service;

import dev.fatec.ecommerce.cupom.model.Cupom;
import dev.fatec.ecommerce.cupom.model.TipoCupom;
import dev.fatec.ecommerce.cupom.repository.CupomRepository;
import dev.fatec.ecommerce.venda.dto.SolicitarTrocaDTO;
import dev.fatec.ecommerce.venda.model.*;
import dev.fatec.ecommerce.venda.repository.TrocaRepository;
import dev.fatec.ecommerce.venda.repository.VendaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrocaServiceTest {

    @Mock
    private TrocaRepository trocaRepository;

    @Mock
    private VendaRepository vendaRepository;

    @Mock
    private CupomRepository cupomRepository;

    @InjectMocks
    private TrocaService trocaService;

    private Venda venda;
    private Troca troca;
    private SolicitarTrocaDTO solicitarTrocaDTO;

    @BeforeEach
    void setUp() {
        venda = new Venda();
        venda.setId(1L);
        venda.setClienteId(1L);
        venda.setCodigoPedido("PED-0001");
        venda.setStatus(StatusVenda.ENTREGUE);

        troca = new Troca();
        troca.setId(1L);
        troca.setCodigoTroca("TRC-0001");
        troca.setVendaId(1L);
        troca.setClienteId(1L);
        troca.setProdutoId(1L);
        troca.setProdutoNome("Whey Protein");
        troca.setQuantidade(1);
        troca.setMotivo("Produto com defeito");
        troca.setValorCredito(new BigDecimal("100.00"));
        troca.setStatus(StatusTroca.SOLICITADA);

        solicitarTrocaDTO = new SolicitarTrocaDTO();
        solicitarTrocaDTO.setVendaId(1L);
        solicitarTrocaDTO.setProdutoId(1L);
        solicitarTrocaDTO.setProdutoNome("Whey Protein");
        solicitarTrocaDTO.setQuantidade(1);
        solicitarTrocaDTO.setMotivo("Produto com defeito");
        solicitarTrocaDTO.setValorCredito(new BigDecimal("100.00"));
    }

    /* =================================================================
     * CT-16: Usuário solicita troca
     * ================================================================= */
    @Test
    @DisplayName("CT-16: Usuário solicitar troca de um item do pedido")
    void solicitar_DeveCriarTrocaComStatusSolicitada() {
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(trocaRepository.save(any(Troca.class))).thenAnswer(i -> {
            Troca t = i.getArgument(0);
            t.setCodigoTroca("TRC-0001");
            return t;
        });

        Troca resultado = trocaService.solicitar(solicitarTrocaDTO);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getVendaId());
        assertEquals(1L, resultado.getClienteId());
        assertEquals(1L, resultado.getProdutoId());
        assertEquals("Whey Protein", resultado.getProdutoNome());
        assertEquals(1, resultado.getQuantidade());
        assertEquals("Produto com defeito", resultado.getMotivo());
        assertEquals(0, new BigDecimal("100.00").compareTo(resultado.getValorCredito()));
        assertEquals(StatusTroca.SOLICITADA, resultado.getStatus());

        verify(trocaRepository).save(any(Troca.class));
    }

    /* =================================================================
     * CT-17: Admin autoriza troca - sistema gera cupom
     * ================================================================= */
    @Test
    @DisplayName("CT-17: Administrador aceitar troca e sistema gerar cupom de troca")
    void autorizar_DeveAceitarTrocaEGerarCupom() {
        when(trocaRepository.findById(1L)).thenReturn(Optional.of(troca));
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(cupomRepository.save(any(Cupom.class))).thenAnswer(i -> {
            Cupom c = i.getArgument(0);
            c.setId(99L);
            return c;
        });
        when(trocaRepository.save(any(Troca.class))).thenAnswer(i -> i.getArgument(0));

        Troca resultado = trocaService.autorizar(1L);

        assertEquals(StatusTroca.AUTORIZADA, resultado.getStatus());
        assertNotNull(resultado.getCupomGeradoId());
        assertEquals(99L, resultado.getCupomGeradoId().longValue());

        // Verificar que o cupom foi criado
        verify(cupomRepository).save(any(Cupom.class));

        // Verificar que a venda foi atualizada para EM_TROCA
        assertEquals(StatusVenda.EM_TROCA, venda.getStatus());
        verify(vendaRepository).save(venda);
    }

    /* =================================================================
     * CT-18: Admin autoriza troca e sistema gera cupom com valor correto
     * ================================================================= */
    @Test
    @DisplayName("CT-17b: Sistema gerar cupom de troca com valor correto")
    void autorizar_DeveGerarCupomComValorCorreto() {
        when(trocaRepository.findById(1L)).thenReturn(Optional.of(troca));
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(cupomRepository.save(any(Cupom.class))).thenAnswer(i -> i.getArgument(0));
        when(trocaRepository.save(any(Troca.class))).thenAnswer(i -> i.getArgument(0));

        ArgumentCaptor<Cupom> cupomCaptor = ArgumentCaptor.forClass(Cupom.class);
        trocaService.autorizar(1L);

        verify(cupomRepository).save(cupomCaptor.capture());
        Cupom cupomGerado = cupomCaptor.getValue();

        assertEquals(TipoCupom.TROCA, cupomGerado.getTipo());
        assertEquals(0, new BigDecimal("100.00").compareTo(cupomGerado.getValor()));
        assertEquals(1L, cupomGerado.getClienteId().longValue());
        assertEquals(1L, cupomGerado.getVendaOrigemId().longValue());
        assertTrue(cupomGerado.getAtivo());
        assertFalse(cupomGerado.getUtilizado());
        assertNotNull(cupomGerado.getCodigo());
        assertTrue(cupomGerado.getCodigo().contains("TRC-"));
    }

    /* =================================================================
     * CT-19: Admin autoriza troca com status inválido
     * ================================================================= */
    @Test
    @DisplayName("CT-18: Administrador tentar autorizar troca com status inválido")
    void autorizar_ComStatusInvalido_DeveLancarExcecao() {
        troca.setStatus(StatusTroca.AUTORIZADA);
        when(trocaRepository.findById(1L)).thenReturn(Optional.of(troca));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trocaService.autorizar(1L));
        assertTrue(ex.getMessage().contains("não está no status SOLICITADA"));
    }

    /* =================================================================
     * CT-20: Admin recusa troca
     * ================================================================= */
    @Test
    @DisplayName("CT-19: Administrador negar troca / devolução")
    void recusar_DeveNegarTroca() {
        when(trocaRepository.findById(1L)).thenReturn(Optional.of(troca));
        when(trocaRepository.save(any(Troca.class))).thenAnswer(i -> i.getArgument(0));

        Troca resultado = trocaService.recusar(1L);

        assertEquals(StatusTroca.RECUSADA, resultado.getStatus());
        verify(trocaRepository).save(troca);
    }

    /* =================================================================
     * CT-21: Admin conclui troca (confirma recebimento)
     * ================================================================= */
    @Test
    @DisplayName("CT-20: Administrador confirmar recebimento do produto devolvido")
    void concluir_DeveConfirmarRecebimentoEStatusTrocado() {
        troca.setStatus(StatusTroca.AUTORIZADA);
        when(trocaRepository.findById(1L)).thenReturn(Optional.of(troca));
        when(vendaRepository.findById(1L)).thenReturn(Optional.of(venda));
        when(trocaRepository.save(any(Troca.class))).thenAnswer(i -> i.getArgument(0));

        Troca resultado = trocaService.concluir(1L);

        assertEquals(StatusTroca.CONCLUIDA, resultado.getStatus());
        assertEquals(StatusVenda.TROCADO, venda.getStatus());
        verify(vendaRepository).save(venda);
    }

    /* =================================================================
     * CT-22: Concluir troca com status inválido
     * ================================================================= */
    @Test
    @DisplayName("CT-21: Tentar concluir troca que não está AUTORIZADA")
    void concluir_ComStatusInvalido_DeveLancarExcecao() {
        when(trocaRepository.findById(1L)).thenReturn(Optional.of(troca)); // status = SOLICITADA

        RuntimeException ex = assertThrows(RuntimeException.class, () -> trocaService.concluir(1L));
        assertTrue(ex.getMessage().contains("não está no status AUTORIZADA"));
    }
}
