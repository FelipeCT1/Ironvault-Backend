package dev.fatec.ecommerce.produto.repository;

import dev.fatec.ecommerce.produto.model.GrupoPrecificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrupoPrecificacaoRepository extends JpaRepository<GrupoPrecificacao, Long> {
    List<GrupoPrecificacao> findByAtivoTrue();
}
