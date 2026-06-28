package com.bp.mscuentas.domain.port.in;

import com.bp.mscuentas.domain.model.Cuenta;

public interface ActualizarCuentaUseCase {
    Cuenta actualizar(Long id, Cuenta cuenta);
    Cuenta actualizarParcial(Long id, Cuenta cuenta);
}