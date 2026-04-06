package dev.fatec.ecommerce.produto.repository;

import dev.fatec.ecommerce.produto.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByAtivoTrue();
    boolean existsByNome(String nome);
    Optional<Categoria> findByNome(String nome);
}
