package dev.fatec.ecommerce.cliente.service;

import dev.fatec.ecommerce.cliente.model.Cliente;
import dev.fatec.ecommerce.cliente.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;

    public Cliente salvar(Cliente cliente) {
        validarEnderecos(cliente);
        if (cliente.getSenha() == null || cliente.getSenha().isBlank()) {
            throw new RuntimeException("Senha é obrigatória");
        }
        if (cliente.getRanking() == null) cliente.setRanking(0);
        cliente.setSenha(passwordEncoder.encode(cliente.getSenha()));
        return repository.save(cliente);
    }

    public Cliente buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
    }

    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente existente = buscarPorId(id);

        existente.setNome(clienteAtualizado.getNome());
        existente.setCpf(clienteAtualizado.getCpf());
        existente.setEmail(clienteAtualizado.getEmail());
        existente.setDataNascimento(clienteAtualizado.getDataNascimento());
        existente.setGenero(clienteAtualizado.getGenero());
        existente.setTipoTelefone(clienteAtualizado.getTipoTelefone());
        existente.setDdd(clienteAtualizado.getDdd());
        existente.setNumeroTelefone(clienteAtualizado.getNumeroTelefone());

        if (clienteAtualizado.getSenha() != null && !clienteAtualizado.getSenha().isBlank()) {
            existente.setSenha(passwordEncoder.encode(clienteAtualizado.getSenha()));
        }

        if (clienteAtualizado.getEnderecos() != null) {
            existente.getEnderecos().clear();
            existente.getEnderecos().addAll(clienteAtualizado.getEnderecos());
        }

        if (clienteAtualizado.getCartoes() != null) {
            existente.getCartoes().clear();
            existente.getCartoes().addAll(clienteAtualizado.getCartoes());
        }

        validarEnderecos(existente);
        return repository.save(existente);
    }

    public void inativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(false);
        repository.save(cliente);
    }

    public void ativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(true);
        repository.save(cliente);
    }

    public List<Cliente> consultaClientes() {
        return repository.findAll();
    }

    public List<Cliente> buscarComFiltros(String nome, String cpf, String email) {
        return repository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (nome != null && !nome.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nome")), "%" + nome.toLowerCase() + "%"));
            }
            if (cpf != null && !cpf.isEmpty()) {
                predicates.add(cb.equal(root.get("cpf"), cpf));
            }
            if (email != null && !email.isEmpty()) {
                predicates.add(cb.equal(root.get("email"), email));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        });
    }

    public void atualizarSenha(Long id, String novaSenha, String confirmacaoSenha) {
        if (!novaSenha.equals(confirmacaoSenha)) {
            throw new RuntimeException("As senhas não coincidem.");
        }

        Cliente cliente = buscarPorId(id);
        cliente.setSenha(passwordEncoder.encode(novaSenha));
        repository.save(cliente);
    }

    private void validarEnderecos(Cliente cliente) {
        if (cliente.getEnderecos() == null || cliente.getEnderecos().isEmpty()) {
            throw new RuntimeException("Cliente deve ter no mínimo um endereço de entrega e um de cobrança.");
        }
        boolean temEntrega = cliente.getEnderecos().stream().anyMatch(e -> e.isEhEntrega());
        boolean temCobranca = cliente.getEnderecos().stream().anyMatch(e -> e.isEhCobranca());

        if (!temEntrega || !temCobranca) {
            throw new RuntimeException("Cliente deve ter no mínimo um endereço de entrega e um de cobrança.");
        }
    }
}
