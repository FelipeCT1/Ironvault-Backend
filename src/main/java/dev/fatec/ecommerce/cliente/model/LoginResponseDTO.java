package dev.fatec.ecommerce.cliente.model;

public record LoginResponseDTO(
        Long id,
        String nome,
        String email,
        Papel papel,
        Boolean ativo
) {}
