package com.bp.mscuentas.domain.port.in;

import com.bp.mscuentas.domain.model.Movimiento;

public interface RegistrarMovimientoUseCase {
    Movimiento registrar(Movimiento movimiento);
}