package dev.fatec.ecommerce.produto;

import dev.fatec.ecommerce.produto.model.Produto;
import dev.fatec.ecommerce.produto.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService service;

    @GetMapping
    public ResponseEntity<List<Produto>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/busca")
    public ResponseEntity<List<Produto>> buscar(
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Long categoriaId) {
        return ResponseEntity.ok(service.buscarComFiltros(nome, marca, categoriaId));
    }

    @PostMapping
    public ResponseEntity<Produto> criar(@Valid @RequestBody Produto produto) {
        return ResponseEntity.ok(service.salvar(produto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(@PathVariable Long id, @Valid @RequestBody Produto produto) {
        return ResponseEntity.ok(service.atualizar(id, produto));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        service.inativar(id, motivo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(
            @PathVariable Long id,
            @RequestParam(required = false) String motivo) {
        service.ativar(id, motivo);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estoque")
    public ResponseEntity<Void> atualizarEstoque(
            @PathVariable Long id,
            @RequestParam Integer quantidade) {
        service.atualizarEstoque(id, quantidade);
        return ResponseEntity.noContent().build();
    }
}
