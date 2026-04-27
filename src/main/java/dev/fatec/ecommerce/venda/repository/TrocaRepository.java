package dev.fatec.ecommerce.venda.repository;

import dev.fatec.ecommerce.venda.model.Troca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrocaRepository extends JpaRepository<Troca, Long> {

    List<Troca> findByClienteIdOrderByDataCriacaoDesc(Long clienteId);

    List<Troca> findByVendaId(Long vendaId);
}
