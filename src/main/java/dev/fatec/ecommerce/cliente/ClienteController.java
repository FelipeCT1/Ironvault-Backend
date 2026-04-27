package dev.fatec.ecommerce.cliente;

import dev.fatec.ecommerce.cliente.model.AlterarSenhaDTO;
import dev.fatec.ecommerce.cliente.model.Cliente;
import dev.fatec.ecommerce.cliente.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    public ResponseEntity<Cliente> criar(@Valid @RequestBody Cliente cliente) {
        return ResponseEntity.ok(service.salvar(cliente));
    }

    @GetMapping
    public ResponseEntity<List<Cliente>> listarTodos() {
        return ResponseEntity.ok(service.consultaClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> atualizar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        return ResponseEntity.ok(service.atualizar(id, cliente));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable Long id) {
        service.ativar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/busca")
    public List<Cliente> buscar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String email) {
        return service.buscarComFiltros(nome, cpf, email);
    }

    @PatchMapping("/{id}/alterar-senha")
    public ResponseEntity<Void> alterarSenha(@PathVariable Long id, @Valid @RequestBody AlterarSenhaDTO senhaNova) {
        service.atualizarSenha(id, senhaNova.novaSenha(), senhaNova.confirmacaoSenha());
        return ResponseEntity.noContent().build();
    }
}
