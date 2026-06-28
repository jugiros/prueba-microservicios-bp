package com.bp.mscuentas.domain.port.in;

import com.bp.mscuentas.domain.model.Cuenta;

public interface CrearCuentaUseCase {
    Cuenta crear(Cuenta cuenta);
}