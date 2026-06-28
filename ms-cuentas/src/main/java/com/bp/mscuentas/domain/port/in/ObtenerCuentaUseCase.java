package com.bp.mscuentas.domain.port.in;

import com.bp.mscuentas.domain.model.Cuenta;
import java.util.List;

public interface ObtenerCuentaUseCase {
    Cuenta obtenerPorId(Long id);
    List<Cuenta> obtenerTodas();
    List<Cuenta> obtenerPorClienteId(Long clienteId);
}