package com.bp.mscuentas.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovimientoJpaRepository extends JpaRepository<MovimientoEntity, Long> {

    List<MovimientoEntity> findByCuentaId(Long cuentaId);

    List<MovimientoEntity> findByCuentaIdAndFechaBetween(
            Long cuentaId,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin);
}