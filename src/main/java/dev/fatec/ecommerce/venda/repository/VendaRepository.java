package dev.fatec.ecommerce.venda.repository;

import dev.fatec.ecommerce.venda.model.Venda;
import dev.fatec.ecommerce.venda.model.StatusVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Long> {

    Optional<Venda> findByCodigoPedido(String codigoPedido);

    List<Venda> findByClienteId(Long clienteId);

    List<Venda> findByStatus(StatusVenda status);

    @Query("SELECT v FROM Venda v WHERE v.clienteId = :clienteId ORDER BY v.dataCriacao DESC")
    List<Venda> findByClienteIdOrderByDataCriacaoDesc(@Param("clienteId") Long clienteId);

    @Query("SELECT COUNT(v) + 1 FROM Venda v")
    Long getNextPedidoNumber();
}
