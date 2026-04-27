package dev.fatec.ecommerce.venda;

import dev.fatec.ecommerce.venda.dto.SolicitarTrocaDTO;
import dev.fatec.ecommerce.venda.model.Troca;
import dev.fatec.ecommerce.venda.service.TrocaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trocas")
@RequiredArgsConstructor
public class TrocaController {

    private final TrocaService service;

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Troca>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarPorCliente(clienteId));
    }

    @GetMapping
    public ResponseEntity<List<Troca>> listarTodas() {
        return ResponseEntity.ok(service.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Troca> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<Troca> solicitar(@Valid @RequestBody SolicitarTrocaDTO dto) {
        return ResponseEntity.ok(service.solicitar(dto));
    }

    @PatchMapping("/{id}/autorizar")
    public ResponseEntity<Troca> autorizar(@PathVariable Long id) {
        return ResponseEntity.ok(service.autorizar(id));
    }

    @PatchMapping("/{id}/recusar")
    public ResponseEntity<Troca> recusar(@PathVariable Long id) {
        return ResponseEntity.ok(service.recusar(id));
    }

    @PatchMapping("/{id}/concluir")
    public ResponseEntity<Troca> concluir(@PathVariable Long id) {
        return ResponseEntity.ok(service.concluir(id));
    }
}
