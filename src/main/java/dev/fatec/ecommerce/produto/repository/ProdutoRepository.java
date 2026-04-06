package dev.fatec.ecommerce.produto.repository;

import dev.fatec.ecommerce.produto.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>, JpaSpecificationExecutor<Produto> {

    List<Produto> findByAtivoTrue();

    List<Produto> findByCategoriaId(Long categoriaId);

    Optional<Produto> findByCodigoBarras(String codigoBarras);

    @Query("SELECT p FROM Produto p WHERE " +
           "(:nome IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))) AND " +
           "(:marca IS NULL OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :marca, '%'))) AND " +
           "(:categoriaId IS NULL OR p.categoria.id = :categoriaId) AND " +
           "p.ativo = true")
    List<Produto> buscarComFiltros(@Param("nome") String nome,
                                    @Param("marca") String marca,
                                    @Param("categoriaId") Long categoriaId);

    @Query("SELECT p FROM Produto p WHERE p.estoque = 0 AND p.ativo = true")
    List<Produto> findProdutosSemEstoque();

    @Query(value = "SELECT p.* FROM produto p WHERE p.ativo = true AND p.estoque > 0 " +
           "AND p.id NOT IN (SELECT DISTINCT v.produto_id FROM venda_item v " +
           "WHERE v.data_venda < DATE_SUB(CURDATE(), INTERVAL :diasSemVenda DAY))", nativeQuery = true)
    List<Produto> findProdutosSemVendaRecente(@Param("diasSemVenda") int diasSemVenda);
}
