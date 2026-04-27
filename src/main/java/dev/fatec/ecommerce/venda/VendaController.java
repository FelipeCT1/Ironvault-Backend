package dev.fatec.ecommerce.venda;

import dev.fatec.ecommerce.venda.dto.FinalizarCompraDTO;
import dev.fatec.ecommerce.venda.model.Venda;
import dev.fatec.ecommerce.venda.service.VendaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService service;

    @GetMapping
    public ResponseEntity<List<Venda>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Venda>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venda> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Venda> buscarPorCodigo(@PathVariable String codigo) {
        return service.buscarPorCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Venda> finalizarCompra(@Valid @RequestBody FinalizarCompraDTO dto) {
        return ResponseEntity.ok(service.finalizarCompra(dto));
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<Void> aprovar(@PathVariable Long id) {
        service.aprovar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reprovar")
    public ResponseEntity<Void> reprovar(@PathVariable Long id) {
        service.reprovar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/despachar")
    public ResponseEntity<Void> despachar(@PathVariable Long id) {
        service.despachar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/entregar")
    public ResponseEntity<Void> entregar(@PathVariable Long id) {
        service.entregar(id);
        return ResponseEntity.noContent().build();
    }
}
