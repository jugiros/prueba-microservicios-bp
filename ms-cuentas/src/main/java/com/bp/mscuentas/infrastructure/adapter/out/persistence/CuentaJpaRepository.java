package com.bp.mscuentas.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CuentaJpaRepository extends JpaRepository<CuentaEntity, Long> {

    Optional<CuentaEntity> findByNumeroCuenta(String numeroCuenta);
    List<CuentaEntity> findByClienteId(Long clienteId);
    boolean existsByNumeroCuenta(String numeroCuenta);
}