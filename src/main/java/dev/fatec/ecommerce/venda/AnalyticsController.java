package dev.fatec.ecommerce.venda;

import dev.fatec.ecommerce.cliente.model.Papel;
import dev.fatec.ecommerce.cliente.service.AuthService;
import dev.fatec.ecommerce.venda.dto.VendasPorCategoriaDTO;
import dev.fatec.ecommerce.venda.service.AnalyticsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AuthService authService;

    @GetMapping("/vendas-por-periodo")
    public ResponseEntity<List<VendasPorCategoriaDTO>> vendasPorPeriodo(
            @RequestParam("categoriaIds") List<Long> categoriaIds,
            @RequestParam("dataInicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam("dataFim") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            HttpServletRequest request) {

        Papel papel = authService.getPapelLogado(request);
        if (papel != Papel.ADMIN) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(analyticsService.vendasPorPeriodo(categoriaIds, dataInicio, dataFim));
    }
}
