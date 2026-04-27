package dev.fatec.ecommerce.venda.service;

import dev.fatec.ecommerce.cupom.repository.CupomRepository;
import dev.fatec.ecommerce.produto.repository.ProdutoRepository;
import dev.fatec.ecommerce.venda.dto.FinalizarCompraDTO;
import dev.fatec.ecommerce.venda.dto.ItemCompraDTO;
import dev.fatec.ecommerce.venda.dto.PagamentoCartaoDTO;
import dev.fatec.ecommerce.venda.model.*;
import dev.fatec.ecommerce.venda.repository.VendaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final CupomRepository cupomRepository;

    @Transactional(readOnly = true)
    public List<Venda> listarTodas() {
        return vendaRepository.findAllFetched();
    }

    @Transactional(readOnly = true)
    public List<Venda> listarPorCliente(Long clienteId) {
        return vendaRepository.findByClienteIdFetched(clienteId);
    }

    @Transactional(readOnly = true)
    public Optional<Venda> buscarPorId(Long id) {
        return vendaRepository.findByIdFetched(id);
    }

    @Transactional(readOnly = true)
    public Optional<Venda> buscarPorCodigo(String codigoPedido) {
        return vendaRepository.findByCodigoPedidoFetched(codigoPedido);
    }

    @Transactional
    public Venda finalizarCompra(FinalizarCompraDTO dto) {
        Venda venda = new Venda();
        venda.setClienteId(dto.getClienteId());
        venda.setCodigoPedido(gerarCodigoPedido());

        // Endereço de entrega
        if (dto.getEnderecoEntrega() != null) {
            EnderecoEntrega endereco = new EnderecoEntrega();
            endereco.setApelido(dto.getEnderecoEntrega().getApelido());
            endereco.setTipoLogradouro(dto.getEnderecoEntrega().getTipoLogradouro());
            endereco.setLogradouro(dto.getEnderecoEntrega().getLogradouro());
            endereco.setNumero(dto.getEnderecoEntrega().getNumero());
            endereco.setBairro(dto.getEnderecoEntrega().getBairro());
            endereco.setCep(dto.getEnderecoEntrega().getCep());
            endereco.setCidade(dto.getEnderecoEntrega().getCidade());
            endereco.setEstado(dto.getEnderecoEntrega().getEstado());
            endereco.setPais(dto.getEnderecoEntrega().getPais());
            venda.setEnderecoEntrega(endereco);
        }

        // Frete
        if (dto.getFrete() != null) {
            venda.setFreteTipo(dto.getFrete().getTipo());
            venda.setFretePrazoDias(dto.getFrete().getPrazoDias());
            venda.setFreteValor(dto.getFrete().getValor());
        }

        // Itens
        BigDecimal subtotal = BigDecimal.ZERO;
        for (ItemCompraDTO itemDto : dto.getItens()) {
            ItemVenda item = new ItemVenda();
            item.setVenda(venda);
            item.setProdutoId(itemDto.getProdutoId());
            item.setProdutoNome(itemDto.getProdutoNome());
            item.setQuantidade(itemDto.getQuantidade());
            item.setPrecoUnitario(itemDto.getPrecoUnitario());
            item.setSubtotal(itemDto.getPrecoUnitario().multiply(BigDecimal.valueOf(itemDto.getQuantidade())));
            venda.getItens().add(item);

            subtotal = subtotal.add(item.getSubtotal());

            // Dar baixa no estoque
            produtoRepository.findById(itemDto.getProdutoId()).ifPresent(produto -> {
                produto.setEstoque(produto.getEstoque() - itemDto.getQuantidade());
                produtoRepository.save(produto);
            });
        }
        venda.setSubtotal(subtotal);

        // Cupom promocional
        if (dto.getCupomPromocionalId() != null) {
            cupomRepository.findById(dto.getCupomPromocionalId()).ifPresent(cupom -> {
                venda.setCupomPromocionalId(cupom.getId());
                venda.setCupomPromocionalCodigo(cupom.getCodigo());
                venda.setDescontoPromocional(cupom.getValor());
                cupom.setUtilizado(true);
                cupom.setDataUtilizacao(LocalDateTime.now());
                cupomRepository.save(cupom);
            });
        }

        // Cupons de troca
        BigDecimal descontoTroca = BigDecimal.ZERO;
        if (dto.getCuponsTrocaIds() != null) {
            for (Long cupomId : dto.getCuponsTrocaIds()) {
                cupomRepository.findById(cupomId).ifPresent(cupom -> {
                    venda.getCuponsTrocaIds().add(cupomId);
                    cupom.setUtilizado(true);
                    cupom.setDataUtilizacao(LocalDateTime.now());
                    cupomRepository.save(cupom);
                });
            }
            if (!dto.getCuponsTrocaIds().isEmpty()) {
                descontoTroca = dto.getCuponsTrocaIds().stream()
                        .map(id -> cupomRepository.findById(id))
                        .filter(Optional::isPresent)
                        .map(opt -> opt.get().getValor())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
        }
        venda.setDescontoTroca(descontoTroca);

        // Pagamentos com cartão
        if (dto.getPagamentosCartao() != null) {
            for (PagamentoCartaoDTO pagamentoDto : dto.getPagamentosCartao()) {
                PagamentoCartao pagamento = new PagamentoCartao();
                pagamento.setVenda(venda);
                pagamento.setCartaoId(pagamentoDto.getCartaoId());
                pagamento.setBandeira(pagamentoDto.getBandeira());
                pagamento.setUltimosDigitos(pagamentoDto.getUltimosDigitos());
                pagamento.setValor(pagamentoDto.getValor());
                venda.getPagamentosCartao().add(pagamento);
            }
        }

        // Calcular total
        venda.calcularTotais();
        venda.setStatus(StatusVenda.EM_PROCESSAMENTO);

        return vendaRepository.save(venda);
    }

    @Transactional
    public void aprovar(Long vendaId) {
        Venda venda = vendaRepository.findByIdFetched(vendaId)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));
        venda.setStatus(StatusVenda.APROVADA);
        vendaRepository.save(venda);
    }

    @Transactional
    public void reprovar(Long vendaId) {
        Venda venda = vendaRepository.findByIdFetched(vendaId)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));
        venda.setStatus(StatusVenda.REPROVADA);
        for (ItemVenda item : venda.getItens()) {
            produtoRepository.findById(item.getProdutoId()).ifPresent(produto -> {
                produto.setEstoque(produto.getEstoque() + item.getQuantidade());
                produtoRepository.save(produto);
            });
        }
        vendaRepository.save(venda);
    }

    @Transactional
    public void despachar(Long vendaId) {
        Venda venda = vendaRepository.findByIdFetched(vendaId)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));
        venda.setStatus(StatusVenda.EM_TRANSITO);
        vendaRepository.save(venda);
    }

    @Transactional
    public void entregar(Long vendaId) {
        Venda venda = vendaRepository.findByIdFetched(vendaId)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));
        venda.setStatus(StatusVenda.ENTREGUE);
        vendaRepository.save(venda);
    }

    private String gerarCodigoPedido() {
        Long nextNum = vendaRepository.getNextPedidoNumber();
        return "PED-" + String.format("%04d", nextNum);
    }
}
