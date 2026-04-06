package dev.fatec.ecommerce.cupom.repository;

import dev.fatec.ecommerce.cupom.model.Cupom;
import dev.fatec.ecommerce.cupom.model.TipoCupom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CupomRepository extends JpaRepository<Cupom, Long> {

    Optional<Cupom> findByCodigo(String codigo);

    List<Cupom> findByClienteIdAndUtilizadoFalseAndAtivoTrue(Long clienteId);

    List<Cupom> findByTipoAndAtivoTrue(TipoCupom tipo);

    List<Cupom> findByClienteIdAndTipoAndUtilizadoFalseAndAtivoTrue(Long clienteId, TipoCupom tipo);
}
