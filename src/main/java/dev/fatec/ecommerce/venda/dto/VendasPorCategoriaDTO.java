package dev.fatec.ecommerce.venda.dto;

import java.util.List;

public class VendasPorCategoriaDTO {
    private Long categoriaId;
    private String categoriaNome;
    private List<DadosMensaisDTO> dados;

    public VendasPorCategoriaDTO() {}

    public VendasPorCategoriaDTO(Long categoriaId, String categoriaNome, List<DadosMensaisDTO> dados) {
        this.categoriaId = categoriaId;
        this.categoriaNome = categoriaNome;
        this.dados = dados;
    }

    public Long getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Long categoriaId) { this.categoriaId = categoriaId; }
    public String getCategoriaNome() { return categoriaNome; }
    public void setCategoriaNome(String categoriaNome) { this.categoriaNome = categoriaNome; }
    public List<DadosMensaisDTO> getDados() { return dados; }
    public void setDados(List<DadosMensaisDTO> dados) { this.dados = dados; }
}
