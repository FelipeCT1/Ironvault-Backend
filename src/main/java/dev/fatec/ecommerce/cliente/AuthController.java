package dev.fatec.ecommerce.cliente;

import dev.fatec.ecommerce.cliente.model.CartaoCredito;
import dev.fatec.ecommerce.cliente.model.CartaoResponseDTO;
import dev.fatec.ecommerce.cliente.model.Cliente;
import dev.fatec.ecommerce.cliente.model.Endereco;
import dev.fatec.ecommerce.cliente.model.LoginRequestDTO;
import dev.fatec.ecommerce.cliente.model.LoginResponseDTO;
import dev.fatec.ecommerce.cliente.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto,
            HttpServletRequest request) {
        return ResponseEntity.ok(authService.login(dto.email(), dto.senha(), request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        authService.logout(request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<Cliente> me(HttpServletRequest request) {
        return ResponseEntity.ok(authService.getClienteLogado(request));
    }

    @GetMapping("/enderecos")
    public ResponseEntity<List<Endereco>> enderecos(HttpServletRequest request) {
        return ResponseEntity.ok(authService.getEnderecosLogado(request));
    }

    @GetMapping("/cartoes")
    public ResponseEntity<List<CartaoResponseDTO>> cartoes(HttpServletRequest request) {
        List<CartaoCredito> cartoes = authService.getCartoesLogado(request);
        List<CartaoResponseDTO> response = cartoes.stream()
                .map(CartaoResponseDTO::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/cartoes")
    public ResponseEntity<CartaoResponseDTO> adicionarCartao(
            @Valid @RequestBody CartaoCredito cartao,
            HttpServletRequest request) {
        CartaoCredito saved = authService.adicionarCartaoLogado(request, cartao);
        return ResponseEntity.ok(CartaoResponseDTO.from(saved));
    }
}
