package dev.fatec.ecommerce.cliente.model;

public record CartaoResponseDTO(
    Long id,
    String bandeira,
    String ultimosDigitos,
    String nomeImpresso,
    boolean preferencial
) {
    public static CartaoResponseDTO from(CartaoCredito cartao) {
        return new CartaoResponseDTO(
            cartao.getId(),
            cartao.getBandeira(),
            cartao.getUltimosDigitos(),
            cartao.getNomeImpresso(),
            cartao.isPreferencial()
        );
    }
}
