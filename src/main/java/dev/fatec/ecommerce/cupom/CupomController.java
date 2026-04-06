package dev.fatec.ecommerce.cupom;

import dev.fatec.ecommerce.cupom.model.Cupom;
import dev.fatec.ecommerce.cupom.service.CupomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cupons")
@RequiredArgsConstructor
public class CupomController {

    private final CupomService service;

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<Cupom> validarCupom(@PathVariable String codigo) {
        return service.validarCupom(codigo)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/troca/cliente/{clienteId}")
    public ResponseEntity<List<Cupom>> listarCuponsTrocaCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(service.listarCuponsTrocaCliente(clienteId));
    }

    @GetMapping("/promocionais")
    public ResponseEntity<List<Cupom>> listarCuponsPromocionaisAtivos() {
        return ResponseEntity.ok(service.listarCuponsPromocionaisAtivos());
    }
}
