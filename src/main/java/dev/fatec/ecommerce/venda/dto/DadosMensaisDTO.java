package dev.fatec.ecommerce.venda.dto;

public class DadosMensaisDTO {
    private String anoMes;
    private Integer quantidade;

    public DadosMensaisDTO() {}

    public DadosMensaisDTO(String anoMes, Integer quantidade) {
        this.anoMes = anoMes;
        this.quantidade = quantidade;
    }

    public String getAnoMes() { return anoMes; }
    public void setAnoMes(String anoMes) { this.anoMes = anoMes; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
}
