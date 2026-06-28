package com.bp.mscuentas.domain.port.out;

import com.bp.mscuentas.domain.model.Movimiento;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MovimientoRepositoryPort {
    Movimiento guardar(Movimiento movimiento);
    Optional<Movimiento> buscarPorId(Long id);
    List<Movimiento> buscarPorCuentaId(Long cuentaId);
    List<Movimiento> buscarPorCuentaIdYFechas(Long cuentaId,
                                              LocalDateTime fechaInicio,
                                              LocalDateTime fechaFin);
}