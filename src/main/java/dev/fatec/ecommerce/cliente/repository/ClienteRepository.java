package dev.fatec.ecommerce.cliente.repository;

import dev.fatec.ecommerce.cliente.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, JpaSpecificationExecutor<Cliente> {
    Optional<Cliente> findByEmail(String email);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.enderecos WHERE c.id = :id")
    Optional<Cliente> findWithEnderecosById(@Param("id") Long id);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.cartoes WHERE c.id = :id")
    Optional<Cliente> findWithCartoesById(@Param("id") Long id);

    @Query("SELECT c FROM Cliente c LEFT JOIN FETCH c.enderecos LEFT JOIN FETCH c.cartoes WHERE c.id = :id")
    Optional<Cliente> findWithAllById(@Param("id") Long id);
}