package com.bp.mscuentas.domain.port.in;

import com.bp.mscuentas.domain.model.Movimiento;
import java.util.List;

public interface ObtenerMovimientoUseCase {
    Movimiento obtenerMovimientoPorId(Long id);
    List<Movimiento> obtenerPorCuentaId(Long cuentaId);
}