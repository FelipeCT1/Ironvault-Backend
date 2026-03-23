package dev.fatec.ecommerce.cliente.model;

public record AlterarSenhaDTO(
        String novaSenha,
        String confirmacaoSenha
) {}