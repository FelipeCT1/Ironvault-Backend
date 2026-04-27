package dev.fatec.ecommerce.venda.service;

import dev.fatec.ecommerce.cupom.model.Cupom;
import dev.fatec.ecommerce.cupom.model.TipoCupom;
import dev.fatec.ecommerce.cupom.repository.CupomRepository;
import dev.fatec.ecommerce.venda.dto.SolicitarTrocaDTO;
import dev.fatec.ecommerce.venda.model.StatusTroca;
import dev.fatec.ecommerce.venda.model.Troca;
import dev.fatec.ecommerce.venda.model.Venda;
import dev.fatec.ecommerce.venda.repository.TrocaRepository;
import dev.fatec.ecommerce.venda.repository.VendaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrocaService {

    private final TrocaRepository trocaRepository;
    private final VendaRepository vendaRepository;
    private final CupomRepository cupomRepository;

    public List<Troca> listarPorCliente(Long clienteId) {
        return trocaRepository.findByClienteIdOrderByDataCriacaoDesc(clienteId);
    }

    public List<Troca> listarTodas() {
        return trocaRepository.findAll();
    }

    public Troca buscarPorId(Long id) {
        return trocaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Troca não encontrada"));
    }

    @Transactional
    public Troca solicitar(SolicitarTrocaDTO dto) {
        Venda venda = vendaRepository.findById(dto.getVendaId())
                .orElseThrow(() -> new RuntimeException("Venda não encontrada"));

        Troca troca = new Troca();
        troca.setVendaId(dto.getVendaId());
        troca.setClienteId(venda.getClienteId());
        troca.setProdutoId(dto.getProdutoId());
        troca.setProdutoNome(dto.getProdutoNome());
        troca.setQuantidade(dto.getQuantidade());
        troca.setMotivo(dto.getMotivo());
        troca.setValorCredito(dto.getValorCredito());
        troca.setStatus(StatusTroca.SOLICITADA);

        return trocaRepository.save(troca);
    }

    @Transactional
    public Troca autorizar(Long trocaId) {
        Troca troca = buscarPorId(trocaId);

        if (troca.getStatus() != StatusTroca.SOLICITADA) {
            throw new RuntimeException("Troca não está no status SOLICITADA");
        }

        troca.setStatus(StatusTroca.AUTORIZADA);

        Cupom cupom = new Cupom();
        cupom.setCodigo("TROCA-" + troca.getCodigoTroca());
        cupom.setTipo(TipoCupom.TROCA);
        cupom.setValor(troca.getValorCredito());
        cupom.setValidoAte(LocalDate.now().plusMonths(6));
        cupom.setClienteId(troca.getClienteId());
        cupom.setVendaOrigemId(troca.getVendaId());
        cupom.setAtivo(true);
        cupom = cupomRepository.save(cupom);

        troca.setCupomGeradoId(cupom.getId());
        troca = trocaRepository.save(troca);

        vendaRepository.findById(troca.getVendaId()).ifPresent(venda -> {
            venda.setStatus(dev.fatec.ecommerce.venda.model.StatusVenda.EM_TROCA);
            vendaRepository.save(venda);
        });

        return troca;
    }

    @Transactional
    public Troca recusar(Long trocaId) {
        Troca troca = buscarPorId(trocaId);
        troca.setStatus(StatusTroca.RECUSADA);
        return trocaRepository.save(troca);
    }

    @Transactional
    public Troca concluir(Long trocaId) {
        Troca troca = buscarPorId(trocaId);

        if (troca.getStatus() != StatusTroca.AUTORIZADA) {
            throw new RuntimeException("Troca não está no status AUTORIZADA");
        }

        troca.setStatus(StatusTroca.CONCLUIDA);

        vendaRepository.findById(troca.getVendaId()).ifPresent(venda -> {
            venda.setStatus(dev.fatec.ecommerce.venda.model.StatusVenda.TROCADO);
            vendaRepository.save(venda);
        });

        return trocaRepository.save(troca);
    }
}
