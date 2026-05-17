package dev.fatec.ecommerce.cupom.service;

import dev.fatec.ecommerce.cupom.model.Cupom;
import dev.fatec.ecommerce.cupom.model.TipoCupom;
import dev.fatec.ecommerce.cupom.repository.CupomRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CupomServiceTest {

    @Mock
    private CupomRepository cupomRepository;

    @InjectMocks
    private CupomService cupomService;

    @Captor
    private ArgumentCaptor<Cupom> cupomCaptor;

    /* =================================================================
     * CT-22: Sistema gera cupom de troca
     * ================================================================= */
    @Test
    @DisplayName("CT-22: Sistema gerar cupom de troca com dados corretos")
    void criarCupomTroca_DeveCriarCupomValido() {
        when(cupomRepository.save(any(Cupom.class))).thenAnswer(i -> {
            Cupom c = i.getArgument(0);
            c.setId(1L);
            return c;
        });

        Cupom resultado = cupomService.criarCupomTroca(1L, 100L, new BigDecimal("50.00"));

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId().longValue());
        assertEquals(TipoCupom.TROCA, resultado.getTipo());
        assertEquals(0, new BigDecimal("50.00").compareTo(resultado.getValor()));
        assertEquals(1L, resultado.getClienteId().longValue());
        assertEquals(100L, resultado.getVendaOrigemId().longValue());
        assertTrue(resultado.getAtivo());
        assertFalse(resultado.getUtilizado());
        assertNotNull(resultado.getCodigo());
        assertTrue(resultado.getCodigo().startsWith("TROCA"));

        // Validar que a data de validade é 1 ano da data atual
        LocalDate expectedValidade = LocalDate.now().plusYears(1);
        assertEquals(expectedValidade, resultado.getValidoAte());

        verify(cupomRepository).save(any(Cupom.class));
    }

    /* =================================================================
     * CT-22b: Validação de cupom inativo ou expirado
     * ================================================================= */
    @Test
    @DisplayName("CT-22b: Cupom expirado não é válido")
    void validarCupom_Expirado_DeveRetornarVazio() {
        Cupom cupomExpirado = new Cupom();
        cupomExpirado.setCodigo("EXPIRADO");
        cupomExpirado.setTipo(TipoCupom.PROMOCIONAL);
        cupomExpirado.setValor(new BigDecimal("10.00"));
        cupomExpirado.setValidoAte(LocalDate.now().minusDays(1));
        cupomExpirado.setUtilizado(false);
        cupomExpirado.setAtivo(true);

        when(cupomRepository.findByCodigo("EXPIRADO")).thenReturn(java.util.Optional.of(cupomExpirado));

        var result = cupomService.validarCupom("EXPIRADO");

        assertTrue(result.isEmpty());
    }

    /* =================================================================
     * CT-22c: Cupom já utilizado não é válido
     * ================================================================= */
    @Test
    @DisplayName("CT-22c: Cupom já utilizado não é válido")
    void validarCupom_Utilizado_DeveRetornarVazio() {
        Cupom cupomUtilizado = new Cupom();
        cupomUtilizado.setCodigo("USADO");
        cupomUtilizado.setTipo(TipoCupom.PROMOCIONAL);
        cupomUtilizado.setValor(new BigDecimal("10.00"));
        cupomUtilizado.setValidoAte(LocalDate.now().plusDays(30));
        cupomUtilizado.setUtilizado(true);
        cupomUtilizado.setAtivo(true);

        when(cupomRepository.findByCodigo("USADO")).thenReturn(java.util.Optional.of(cupomUtilizado));

        var result = cupomService.validarCupom("USADO");

        assertTrue(result.isEmpty());
    }
}
