package dev.fatec.ecommerce.recomendacao.controller;

import dev.fatec.ecommerce.recomendacao.dto.MensagemChatDTO;
import dev.fatec.ecommerce.recomendacao.dto.RespostaChatDTO;
import dev.fatec.ecommerce.recomendacao.service.RecomendacaoService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recomendacao")
@RequiredArgsConstructor
public class RecomendacaoController {

    private final RecomendacaoService recomendacaoService;

    @PostMapping("/chat")
    public ResponseEntity<RespostaChatDTO> chat(
            @Valid @RequestBody MensagemChatDTO dto,
            HttpSession session
    ) {
        RespostaChatDTO resposta = recomendacaoService.processarMensagem(dto.mensagem(), session);
        return ResponseEntity.ok(resposta);
    }
}
