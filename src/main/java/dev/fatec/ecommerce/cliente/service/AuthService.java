package dev.fatec.ecommerce.cliente.service;

import dev.fatec.ecommerce.cliente.model.*;
import dev.fatec.ecommerce.cliente.repository.ClienteRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final ClienteRepository repository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDTO login(String email, String senha, HttpServletRequest request) {
        Cliente cliente = repository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

        if (!passwordEncoder.matches(senha, cliente.getSenha())) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        if (!cliente.getAtivo()) {
            throw new RuntimeException("Conta inativa. Contate o administrador.");
        }

        HttpSession session = request.getSession(true);
        session.setAttribute("clienteId", cliente.getId());
        session.setAttribute("clientePapel", cliente.getPapel().name());

        return new LoginResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getPapel(),
                cliente.getAtivo()
        );
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public Cliente getClienteLogado(HttpServletRequest request) {
        Long clienteId = getClienteIdFromSession(request);
        return repository.findById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Endereco> getEnderecosLogado(HttpServletRequest request) {
        Long clienteId = getClienteIdFromSession(request);
        Cliente cliente = repository.findWithEnderecosById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        return cliente.getEnderecos();
    }

    @Transactional(readOnly = true)
    public List<CartaoCredito> getCartoesLogado(HttpServletRequest request) {
        Long clienteId = getClienteIdFromSession(request);
        Cliente cliente = repository.findWithCartoesById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        return cliente.getCartoes();
    }

    @Transactional
    public CartaoCredito adicionarCartaoLogado(HttpServletRequest request, CartaoCredito cartao) {
        Long clienteId = getClienteIdFromSession(request);
        Cliente cliente = repository.findWithCartoesById(clienteId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
        cliente.getCartoes().add(cartao);
        repository.save(cliente);
        return cartao;
    }

    public Papel getPapelLogado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clientePapel") == null) {
            return null;
        }
        return Papel.valueOf((String) session.getAttribute("clientePapel"));
    }

    private Long getClienteIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("clienteId") == null) {
            throw new SecurityException("Nenhum cliente logado");
        }
        return (Long) session.getAttribute("clienteId");
    }
}
