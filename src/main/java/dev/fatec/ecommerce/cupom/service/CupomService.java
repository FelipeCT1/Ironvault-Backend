package dev.fatec.ecommerce.cupom.service;

import dev.fatec.ecommerce.cupom.model.Cupom;
import dev.fatec.ecommerce.cupom.model.TipoCupom;
import dev.fatec.ecommerce.cupom.repository.CupomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CupomService {

    private final CupomRepository cupomRepository;

    public Optional<Cupom> validarCupom(String codigo) {
        return cupomRepository.findByCodigo(codigo.toUpperCase())
            .filter(c -> c.isValido());
    }

    public List<Cupom> listarCuponsTrocaCliente(Long clienteId) {
        return cupomRepository.findByClienteIdAndTipoAndUtilizadoFalseAndAtivoTrue(
            clienteId, TipoCupom.TROCA);
    }

    public List<Cupom> listarCuponsPromocionaisAtivos() {
        return cupomRepository.findByTipoAndAtivoTrue(TipoCupom.PROMOCIONAL);
    }

    @Transactional
    public Cupom criarCupomTroca(Long clienteId, Long vendaOrigemId, java.math.BigDecimal valor) {
        Cupom cupom = new Cupom();
        cupom.setCodigo("TROCA" + System.currentTimeMillis());
        cupom.setTipo(TipoCupom.TROCA);
        cupom.setValor(valor);
        cupom.setValidoAte(LocalDate.now().plusYears(1));
        cupom.setClienteId(clienteId);
        cupom.setVendaOrigemId(vendaOrigemId);
        return cupomRepository.save(cupom);
    }

    @Transactional
    public Cupom criarCupomPromocional(String codigo, java.math.BigDecimal valor, LocalDate validoAte) {
        Cupom cupom = new Cupom();
        cupom.setCodigo(codigo.toUpperCase());
        cupom.setTipo(TipoCupom.PROMOCIONAL);
        cupom.setValor(valor);
        cupom.setValidoAte(validoAte);
        return cupomRepository.save(cupom);
    }

    @Transactional
    public void marcarComoUtilizado(Long cupomId) {
        cupomRepository.findById(cupomId).ifPresent(cupom -> {
            cupom.setUtilizado(true);
            cupom.setDataUtilizacao(java.time.LocalDateTime.now());
            cupomRepository.save(cupom);
        });
    }

    @Transactional
    public void inativar(Long cupomId) {
        cupomRepository.findById(cupomId).ifPresent(cupom -> {
            cupom.setAtivo(false);
            cupomRepository.save(cupom);
        });
    }
}
