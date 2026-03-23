package dev.fatec.ecommerce.cliente;

import dev.fatec.ecommerce.cliente.model.AlterarSenhaDTO;
import dev.fatec.ecommerce.cliente.model.Cliente;
import dev.fatec.ecommerce.cliente.service.ClienteService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/clientes")
public class ClienteController {
    private ClienteService service;

    @PostMapping
    public ResponseEntity<Cliente> criar(@RequestBody Cliente cliente) {
        return ResponseEntity.ok(service.salvar(cliente));
    }

    @GetMapping("/buscaGeral")
    public ResponseEntity<List<Cliente>>  listarTodos() {
        return ResponseEntity.ok(service.consultaClientes());
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Long id) {
        service.inativar(id);
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
    public ResponseEntity<Void> alterarSenha(@PathVariable Long id, @RequestBody AlterarSenhaDTO senhaNova) {
        service.atualizarSenha(id, senhaNova.novaSenha(), senhaNova.confirmacaoSenha());
        return ResponseEntity.noContent().build();
    }
}