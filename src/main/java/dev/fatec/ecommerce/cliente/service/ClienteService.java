package dev.fatec.ecommerce.cliente.service;

import dev.fatec.ecommerce.cliente.model.Cliente;
import dev.fatec.ecommerce.cliente.repository.ClienteRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {
    private final ClienteRepository repository;

    public Cliente salvar(Cliente cliente) {
        validarEnderecos(cliente);
        if (cliente.getRanking() == null) cliente.setRanking(0);

        return repository.save(cliente);
    }

    private void validarEnderecos(Cliente cliente) {
        boolean temEntrega = cliente.getEnderecos().stream().anyMatch(e -> e.isEhEntrega());
        boolean temCobranca = cliente.getEnderecos().stream().anyMatch(e -> e.isEhCobranca());

        if (!temEntrega || !temCobranca) {
            throw new RuntimeException("Cliente deve ter no mínimo um endereço de entrega e um de cobrança.");
        }
    }

    public void inativar(Long id) {
        Cliente cliente = repository.findById(id).orElseThrow();
        cliente.setAtivo(false);
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

        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.setSenha(novaSenha);
        repository.save(cliente);
    }
}